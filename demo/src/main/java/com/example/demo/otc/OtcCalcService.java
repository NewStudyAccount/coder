package com.example.demo.otc;

import com.example.demo.otc.OtcCalcModels.OtcCalcRequest;
import com.example.demo.otc.OtcCalcModels.OtcCalcResponse;
import com.example.demo.otc.OtcCalcModels.OtcItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OtcCalcService {

    private final JdbcTemplate jdbcTemplate;
    private final ProductCenterClient productCenterClient;

    public OtcCalcService(JdbcTemplate jdbcTemplate, ProductCenterClient productCenterClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.productCenterClient = productCenterClient;
    }

    public OtcCalcResponse calculate(OtcCalcRequest req) {
        OtcCalcResponse resp = new OtcCalcResponse();
        long dnQty = computeDnQty(req);
        resp.dnQty = dnQty;
        if (dnQty <= 0) {
            resp.dnLevelOtcProductList = Collections.emptyList();
            resp.orderLevelOtcProductList = Collections.emptyList();
            return resp;
        }

        Optional<OrderLineKey> randomDn = selectRandomDn(req);
        if (randomDn.isEmpty()) {
            resp.dnLevelOtcProductList = Collections.emptyList();
            resp.orderLevelOtcProductList = Collections.emptyList();
            return resp;
        }

        OrderLineKey key = randomDn.get();
        List<ProductRow> products = queryProducts(key.order_id, key.order_line_id);

        List<OtcItem> dnLevelList = new ArrayList<>();
        List<OtcItem> orderLevelList = new ArrayList<>();

        for (ProductRow pr : products) {
            if (!productCenterClient.isDnLevelOtcProduct(pr.product_type_code)) {
                continue;
            }
            List<ElementRow> elements = queryElements(key.order_id, key.order_line_id, pr.product_id, pr.prod_item_id);
            for (ElementRow er : elements) {
                Map<String, Long> attrs = queryElementAttrs(er.element_id, er.element_item_id);

                Long standardFee = attrs.get("standard_fee");
                Long otcFee = attrs.get("otc_fee");
                Long rebateFee = attrs.get("rebate_fee");
                Long qtyAttr = attrs.get("qty");

                long realQty = (qtyAttr != null) ? qtyAttr : dnQty;

                if (otcFee != null) {
                    OtcItem item = baseItem(req.trade_type_code, pr, er, standardFee, otcFee, rebateFee, realQty);
                    Long waived = null;
                    if (standardFee != null) {
                        waived = standardFee - otcFee;
                    }
                    item.waived_fee = waived;
                    item.total_fee = multiplySafe(otcFee, realQty);
                    dnLevelList.add(item);
                }

                if (rebateFee != null) {
                    OtcItem item = baseItem(req.trade_type_code, pr, er, standardFee, null, rebateFee, realQty);
                    Long waived = null;
                    if (standardFee != null && otcFee != null) {
                        waived = standardFee - otcFee;
                    }
                    item.waived_fee = waived;
                    item.total_fee = multiplySafe(rebateFee, realQty);
                    orderLevelList.add(item);
                }
            }
        }

        resp.dnLevelOtcProductList = dnLevelList;
        resp.orderLevelOtcProductList = orderLevelList;
        return resp;
    }

    private long computeDnQty(OtcCalcRequest req) {
        if (req.amendOrderList == null || req.amendOrderList.isEmpty()) return 0L;

        String inSql = req.amendOrderList.stream().map(x -> "?").collect(Collectors.joining(","));
        List<Object> args = new ArrayList<>(req.amendOrderList);

        String commonWhere = " trace_type_code <> '615' and (produce_order_state = '' or produce_order_state = '0') and line_leve in (0,1) ";

        String sqlCount;
        List<Object> params = new ArrayList<>(args);

        if (!Objects.equals(req.cancel_tag, 3)) {
            sqlCount = "select count(1) from oc_order_line where order_id in (" + inSql + ") and trade_type_code = ? and cancel_tag = '0' and " + commonWhere;
            params.add(req.trade_type_code);
        } else {
            sqlCount = "select count(1) from oc_order_line where order_id in (" + inSql + ") and cancel_tag = '3' and " + commonWhere;
        }

        Long count = jdbcTemplate.queryForObject(sqlCount, params.toArray(), Long.class);
        long c = (count == null ? 0L : count);

        long cancelDnQty = (req.cancelDnQty == null ? 0L : req.cancelDnQty);

        if (!Objects.equals(req.cancel_tag, 3)) {
            if (c > 0) {
                return Math.max(0, c - cancelDnQty);
            } else {
                return 0L;
            }
        } else {
            if (c > 0) {
                return c + cancelDnQty;
            } else {
                return c;
            }
        }
    }

    private Optional<OrderLineKey> selectRandomDn(OtcCalcRequest req) {
        if (req.amendOrderList == null || req.amendOrderList.isEmpty()) return Optional.empty();

        String inSql = req.amendOrderList.stream().map(x -> "?").collect(Collectors.joining(","));
        List<Object> args = new ArrayList<>(req.amendOrderList);

        String commonWhere = " trace_type_code <> '615' and (produce_order_state = '' or produce_order_state = '0') and line_leve in (0,1) ";

        String sql;
        List<Object> params = new ArrayList<>(args);

        if (!Objects.equals(req.cancel_tag, 3)) {
            sql = "select order_id, order_line_id from oc_order_line where order_id in (" + inSql + ") and trade_type_code = ? and cancel_tag = '0' and " + commonWhere + " order by rand() limit 1";
            params.add(req.trade_type_code);
        } else {
            sql = "select order_id, order_line_id from oc_order_line where order_id in (" + inSql + ") and cancel_tag = '3' and " + commonWhere + " order by rand() limit 1";
        }

        List<OrderLineKey> list = jdbcTemplate.query(sql, params.toArray(), (rs, rn) -> {
            OrderLineKey k = new OrderLineKey();
            k.order_id = rs.getLong("order_id");
            k.order_line_id = rs.getLong("order_line_id");
            return k;
        });

        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }

    private List<ProductRow> queryProducts(long orderId, long orderLineId) {
        String sql = "select order_id, order_line_id, product_id, package_id, prod_item_id, product_type_code " +
                "from oc_order_product " +
                "where order_id = ? and order_line_id = ? and modify_tag = '0' and end_date > start_date";

        return jdbcTemplate.query(sql, new Object[]{orderId, orderLineId}, (rs, rn) -> {
            ProductRow r = new ProductRow();
            r.order_id = rs.getLong("order_id");
            r.order_line_id = rs.getLong("order_line_id");
            r.product_id = rs.getLong("product_id");
            r.package_id = rs.getLong("package_id");
            r.prod_item_id = rs.getLong("prod_item_id");
            r.product_type_code = rs.getString("product_type_code");
            return r;
        });
    }

    private List<ElementRow> queryElements(long orderId, long orderLineId, long productId, long prodItemId) {
        String sql = "select element_id, element_item_id, product_id, prod_item_id, start_date, end_date " +
                "from oc_order_product_element " +
                "where order_id = ? and order_line_id = ? and product_id = ? and prod_item_id = ? and modify_tag = '0' and end_date > start_date";

        return jdbcTemplate.query(sql, new Object[]{orderId, orderLineId, productId, prodItemId}, (rs, rn) -> {
            ElementRow r = new ElementRow();
            r.element_id = rs.getLong("element_id");
            r.element_item_id = rs.getLong("element_item_id");
            r.product_id = rs.getLong("product_id");
            r.prod_item_id = rs.getLong("prod_item_id");
            Timestamp s = rs.getTimestamp("start_date");
            Timestamp e = rs.getTimestamp("end_date");
            r.start_date = (s == null ? null : s.toLocalDateTime());
            r.end_date = (e == null ? null : e.toLocalDateTime());
            return r;
        });
    }

    private Map<String, Long> queryElementAttrs(long elementId, long elementItemId) {
        String sql = "select attr_code, attr_value from oc_order_element_item " +
                "where element_id = ? and element_item_id = ? and modify_tag = '0' and end_date > now() and attr_code in ('standard_fee','otc_fee','rebate_fee','qty')";

        List<Map.Entry<String, Long>> rows = jdbcTemplate.query(sql, new Object[]{elementId, elementItemId}, (rs, rn) -> {
            String code = rs.getString("attr_code");
            String v = rs.getString("attr_value");
            Long value = null;
            if (v != null && !v.isEmpty()) {
                try {
                    value = Long.parseLong(v);
                } catch (NumberFormatException ignore) {
                }
            }
            return new AbstractMap.SimpleEntry<>(code, value);
        });

        Map<String, Long> map = new HashMap<>();
        for (Map.Entry<String, Long> e : rows) {
            if (e.getValue() != null) map.put(e.getKey(), e.getValue());
        }
        return map;
    }

    private OtcItem baseItem(String tradeType, ProductRow pr, ElementRow er, Long standardFee, Long otcFee, Long rebateFee, long qty) {
        OtcItem item = new OtcItem();
        item.trade_type_code = tradeType;
        item.product_id = pr.product_id;
        item.prod_item_id = pr.prod_item_id;
        item.package_id = pr.package_id;
        item.element_id = er.element_id;
        item.element_item_id = er.element_item_id;
        item.start_date = er.start_date;
        item.end_date = er.end_date;
        item.standard_fee = standardFee;
        item.otc_fee = otcFee;
        item.rebate_fee = rebateFee;
        item.qty = qty;
        return item;
    }

    private Long multiplySafe(Long fee, long qty) {
        if (fee == null) return null;
        try {
            return Math.multiplyExact(fee, qty);
        } catch (ArithmeticException ex) {
            return new java.math.BigDecimal(fee).multiply(new java.math.BigDecimal(qty)).longValueExact();
        }
    }

    private static class OrderLineKey {
        long order_id;
        long order_line_id;
    }

    private static class ProductRow {
        long order_id;
        long order_line_id;
        long product_id;
        long package_id;
        long prod_item_id;
        String product_type_code;
    }

    private static class ElementRow {
        long element_id;
        long element_item_id;
        long product_id;
        long prod_item_id;
        LocalDateTime start_date;
        LocalDateTime end_date;
    }
}