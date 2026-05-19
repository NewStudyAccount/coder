import request from '@/utils/request'
import type { MenuQuery, MenuForm, MenuInfo } from '@/types/menu'
import type { Result } from '@/types/api'

export const getMenuList = (params?: MenuQuery) => {
  return request.get<any, Result<MenuInfo[]>>('/menu/list', { params })
}

export const getMenuTree = (params?: MenuQuery) => {
  return request.get<any, Result<MenuInfo[]>>('/menu/tree', { params })
}

export const getMenuById = (id: number) => {
  return request.get<any, Result<MenuInfo>>(`/menu/${id}`)
}

export const addMenu = (data: MenuForm) => {
  return request.post<any, Result<void>>('/menu', data)
}

export const updateMenu = (data: MenuForm) => {
  return request.put<any, Result<void>>('/menu', data)
}

export const deleteMenu = (id: number) => {
  return request.delete<any, Result<void>>(`/menu/${id}`)
}
