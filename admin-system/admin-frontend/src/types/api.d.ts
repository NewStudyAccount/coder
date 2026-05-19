export interface Result<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResult<T = any> {
  list: T[]
  total: number
}

export interface PageQuery {
  pageNum?: number
  pageSize?: number
}
