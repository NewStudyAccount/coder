import request from '@/utils/request'
import type { CategoryQuery, CategoryForm } from '@/types/category'
import type { Result } from '@/types/api'

export const getCategoryList = (params?: CategoryQuery) => {
  return request.get<any, Result<any[]>>('/category/list', { params })
}

export const getCategoryTree = (params?: CategoryQuery) => {
  return request.get<any, Result<any[]>>('/category/tree', { params })
}

export const getCategoryById = (id: number) => {
  return request.get<any, Result<any>>(`/category/${id}`)
}

export const addCategory = (data: CategoryForm) => {
  return request.post<any, Result<void>>('/category', data)
}

export const updateCategory = (data: CategoryForm) => {
  return request.put<any, Result<void>>('/category', data)
}

export const deleteCategory = (id: number) => {
  return request.delete<any, Result<void>>(`/category/${id}`)
}

export const batchDeleteCategory = (ids: number[]) => {
  return request.delete<any, Result<void>>('/category/batch', { data: ids })
}
