import request from '@/utils/request'
import type { UserQuery, UserForm } from '@/types/user'
import type { Result } from '@/types/api'

export const getUserList = (params?: UserQuery) => {
  return request.get<any, Result<any[]>>('/user/list', { params })
}

export const getUserById = (id: number) => {
  return request.get<any, Result<any>>(`/user/${id}`)
}

export const addUser = (data: UserForm) => {
  return request.post<any, Result<void>>('/user', data)
}

export const updateUser = (data: UserForm) => {
  return request.put<any, Result<void>>('/user', data)
}

export const deleteUser = (id: number) => {
  return request.delete<any, Result<void>>(`/user/${id}`)
}

export const batchDeleteUser = (ids: number[]) => {
  return request.delete<any, Result<void>>('/user/batch', { data: ids })
}

export const getUserRoleIds = (userId: number) => {
  return request.get<any, Result<number[]>>(`/user/${userId}/roles`)
}

export const assignUserRoles = (userId: number, roleIds: number[]) => {
  return request.put<any, Result<void>>(`/user/${userId}/roles`, roleIds)
}

export const resetPassword = (id: number, newPassword: string) => {
  return request.put<any, Result<void>>(`/user/${id}/reset-password`, { newPassword })
}
