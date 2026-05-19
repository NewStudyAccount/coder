export interface RoleInfo {
  id: number
  roleName: string
  roleKey: string
  sort: number
  status: number
  remark: string
  createTime: string
  updateTime: string
  menuIds?: number[]
}

export interface RoleForm {
  id?: number
  roleName: string
  roleKey: string
  sort?: number
  status?: number
  remark?: string
  menuIds?: number[]
}

export interface RoleQuery {
  roleName?: string
  roleKey?: string
  status?: number
}
