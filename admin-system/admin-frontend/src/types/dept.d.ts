export interface DeptInfo {
  id: number
  deptName: string
  parentId: number
  sort: number
  leader: string
  status: number
  createTime: string
  updateTime: string
  children?: DeptInfo[]
}

export interface DeptForm {
  id?: number
  deptName: string
  parentId?: number
  sort?: number
  leader?: string
  status?: number
}

export interface DeptQuery {
  deptName?: string
  status?: number
}
