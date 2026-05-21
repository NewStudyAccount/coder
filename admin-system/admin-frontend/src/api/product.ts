import request from '@/utils/request'
import type { ProductQuery, ProductForm } from '@/types/product'
import type { Result } from '@/types/api'

export const getProductList = (params?: ProductQuery) => {
  return request.get<any, Result<any[]>>('/product/list', { params })
}

export const getProductById = (id: number) => {
  return request.get<any, Result<any>>(`/product/${id}`)
}

export const addProduct = (data: ProductForm) => {
  return request.post<any, Result<void>>('/product', data)
}

export const updateProduct = (data: ProductForm) => {
  return request.put<any, Result<void>>('/product', data)
}

export const deleteProduct = (id: number) => {
  return request.delete<any, Result<void>>(`/product/${id}`)
}

export const batchDeleteProduct = (ids: number[]) => {
  return request.delete<any, Result<void>>('/product/batch', { data: ids })
}

export const updateProductStatus = (id: number, status: number) => {
  return request.put<any, Result<void>>(`/product/${id}/status`, { status })
}

export const getProductSkuList = (productId: number) => {
  return request.get<any, Result<any[]>>(`/product/${productId}/sku`)
}
