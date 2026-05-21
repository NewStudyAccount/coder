import request from '@/utils/request'
import type { CouponQuery, CouponForm } from '@/types/coupon'
import type { Result } from '@/types/api'

export const getCouponList = (params?: CouponQuery) => {
  return request.get<any, Result<any[]>>('/coupon/list', { params })
}

export const getCouponById = (id: number) => {
  return request.get<any, Result<any>>(`/coupon/${id}`)
}

export const addCoupon = (data: CouponForm) => {
  return request.post<any, Result<void>>('/coupon', data)
}

export const updateCoupon = (data: CouponForm) => {
  return request.put<any, Result<void>>('/coupon', data)
}

export const deleteCoupon = (id: number) => {
  return request.delete<any, Result<void>>(`/coupon/${id}`)
}

export const batchDeleteCoupon = (ids: number[]) => {
  return request.delete<any, Result<void>>('/coupon/batch', { data: ids })
}

export const updateCouponStatus = (id: number, status: number) => {
  return request.put<any, Result<void>>(`/coupon/${id}/status`, { status })
}
