export interface Category {
  id: number
  name: string
  parentId: number
  icon: string
  sort: number
  status: number
  createTime: string
  updateTime: string
  children?: Category[]
}

export interface CategoryForm {
  id?: number
  name: string
  parentId?: number
  icon?: string
  sort?: number
  status?: number
}

export interface CategoryQuery {
  name?: string
  status?: number
}
