export interface Coupon {
  id: number
  name: string
  type: number
  discount: number
  minAmount: number
  totalCount: number
  remainCount: number
  startTime: string
  endTime: string
  status: number
  createTime: string
  updateTime: string
}

export interface CouponForm {
  id?: number
  name: string
  type: number
  discount: number
  minAmount?: number
  totalCount: number
  startTime?: string
  endTime?: string
  status?: number
}

export interface CouponQuery {
  name?: string
  type?: number
  status?: number
}
