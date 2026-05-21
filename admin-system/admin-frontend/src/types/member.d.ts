export interface Member {
  id: number
  username: string
  nickname: string
  phone: string
  email: string
  avatar: string
  level: number
  points: number
  balance: number
  status: number
  createTime: string
  updateTime: string
}

export interface MemberForm {
  id?: number
  username: string
  password?: string
  nickname?: string
  phone?: string
  email?: string
  avatar?: string
  level?: number
  status?: number
}

export interface MemberQuery {
  username?: string
  phone?: string
  status?: number
}
