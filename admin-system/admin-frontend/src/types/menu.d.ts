export interface MenuInfo {
  id: number
  menuName: string
  parentId: number
  path: string
  component: string
  icon: string
  sort: number
  menuType: number
  perms: string
  status: number
  createTime: string
  updateTime: string
  children?: MenuInfo[]
}

export interface MenuForm {
  id?: number
  menuName: string
  parentId?: number
  path?: string
  component?: string
  icon?: string
  sort?: number
  menuType: number
  perms?: string
  status?: number
}

export interface MenuQuery {
  menuName?: string
  status?: number
}
