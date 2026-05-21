import request from '@/utils/request'
import type { OrderQuery } from '@/types/order'
import type { Result } from '@/types/api'

export const getOrderList = (params?: OrderQuery) => {
  return request.get<any, Result<any[]>>('/order/list', { params })
}

export const getOrderById = (id: number) => {
  return request.get<any, Result<any>>(`/order/${id}`)
}

export const shipOrder = (id: number) => {
  return request.put<any, Result<void>>(`/order/${id}/ship`)
}

export const cancelOrder = (id: number) => {
  return request.put<any, Result<void>>(`/order/${id}/cancel`)
}

export const refundOrder = (id: number) => {
  return request.put<any, Result<void>>(`/order/${id}/refund`)
}
