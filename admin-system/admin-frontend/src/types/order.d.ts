export interface Order {
  id: number
  orderNo: string
  memberId: number
  totalAmount: number
  payAmount: number
  payTime: string
  payType: number
  status: number
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  remark: string
  createTime: string
  updateTime: string
  memberName: string
  items: OrderItem[]
}

export interface OrderItem {
  id: number
  orderId: number
  productId: number
  skuId: number
  productName: string
  skuName: string
  image: string
  price: number
  quantity: number
  totalPrice: number
}

export interface OrderQuery {
  orderNo?: string
  memberId?: number
  status?: number
}
