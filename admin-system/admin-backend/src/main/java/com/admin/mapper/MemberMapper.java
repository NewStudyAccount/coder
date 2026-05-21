package com.admin.mapper;

import com.admin.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MemberMapper {

    List<Member> selectList(@Param("username") String username, @Param("phone") String phone,
                            @Param("status") Integer status);

    Member selectById(Long id);

    Member selectByUsername(String username);

    int insert(Member member);

    int update(Member member);

    int deleteById(Long id);

    int deleteBatch(List<Long> ids);

    int updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);

    int updatePoints(@Param("id") Long id, @Param("points") Integer points);
}
