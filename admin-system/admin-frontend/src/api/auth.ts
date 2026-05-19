import request from '@/utils/request'
import type { LoginData, LoginResult } from '@/types/user'
import type { MenuInfo } from '@/types/menu'
import type { Result } from '@/types/api'

export const login = (data: LoginData) => {
  return request.post<any, Result<LoginResult>>('/auth/login', data)
}

export const register = (data: any) => {
  return request.post<any, Result<void>>('/auth/register', data)
}

export const getUserInfo = () => {
  return request.get<any, Result<any>>('/auth/userinfo')
}

export const getUserMenus = () => {
  return request.get<any, Result<MenuInfo[]>>('/auth/menus')
}
