export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  deptId: number
  deptName: string
  role: string
  roleName: string
}

export interface UserForm {
  id?: number
  username: string
  password?: string
  nickname: string
  email?: string
  phone?: string
  avatar?: string
  deptId?: number
  status?: number
  roleKey?: string
}

export interface UserQuery {
  username?: string
  status?: number
  deptId?: number
}

export interface LoginData {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  userInfo: UserInfo
}
