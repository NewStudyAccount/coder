import request from '@/utils/request'
import type { MemberQuery, MemberForm } from '@/types/member'
import type { Result } from '@/types/api'

export const getMemberList = (params?: MemberQuery) => {
  return request.get<any, Result<any[]>>('/member/list', { params })
}

export const getMemberById = (id: number) => {
  return request.get<any, Result<any>>(`/member/${id}`)
}

export const addMember = (data: MemberForm) => {
  return request.post<any, Result<void>>('/member', data)
}

export const updateMember = (data: MemberForm) => {
  return request.put<any, Result<void>>('/member', data)
}

export const deleteMember = (id: number) => {
  return request.delete<any, Result<void>>(`/member/${id}`)
}

export const batchDeleteMember = (ids: number[]) => {
  return request.delete<any, Result<void>>('/member/batch', { data: ids })
}

export const updateMemberBalance = (id: number, balance: number) => {
  return request.put<any, Result<void>>(`/member/${id}/balance`, { balance })
}

export const updateMemberPoints = (id: number, points: number) => {
  return request.put<any, Result<void>>(`/member/${id}/points`, { points })
}

export const resetMemberPassword = (id: number, newPassword: string) => {
  return request.put<any, Result<void>>(`/member/${id}/reset-password`, { newPassword })
}
