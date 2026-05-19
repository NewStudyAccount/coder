import request from '@/utils/request'
import type { RoleQuery, RoleForm, RoleInfo } from '@/types/role'
import type { Result } from '@/types/api'

export const getRoleList = (params?: RoleQuery) => {
  return request.get<any, Result<RoleInfo[]>>('/role/list', { params })
}

export const getRoleById = (id: number) => {
  return request.get<any, Result<RoleInfo>>(`/role/${id}`)
}

export const addRole = (data: RoleForm) => {
  return request.post<any, Result<void>>('/role', data)
}

export const updateRole = (data: RoleForm) => {
  return request.put<any, Result<void>>('/role', data)
}

export const deleteRole = (id: number) => {
  return request.delete<any, Result<void>>(`/role/${id}`)
}

export const batchDeleteRole = (ids: number[]) => {
  return request.delete<any, Result<void>>('/role/batch', { data: ids })
}

export const getRoleMenuIds = (roleId: number) => {
  return request.get<any, Result<number[]>>(`/role/${roleId}/menus`)
}

export const assignRoleMenus = (roleId: number, menuIds: number[]) => {
  return request.put<any, Result<void>>(`/role/${roleId}/menus`, menuIds)
}
