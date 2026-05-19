import request from '@/utils/request'
import type { DeptQuery, DeptForm, DeptInfo } from '@/types/dept'
import type { Result } from '@/types/api'

export const getDeptList = (params?: DeptQuery) => {
  return request.get<any, Result<DeptInfo[]>>('/dept/list', { params })
}

export const getDeptTree = (params?: DeptQuery) => {
  return request.get<any, Result<DeptInfo[]>>('/dept/tree', { params })
}

export const getDeptById = (id: number) => {
  return request.get<any, Result<DeptInfo>>(`/dept/${id}`)
}

export const addDept = (data: DeptForm) => {
  return request.post<any, Result<void>>('/dept', data)
}

export const updateDept = (data: DeptForm) => {
  return request.put<any, Result<void>>('/dept', data)
}

export const deleteDept = (id: number) => {
  return request.delete<any, Result<void>>(`/dept/${id}`)
}
