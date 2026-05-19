import request from '@/utils/request'
import type { OssFileQuery } from '@/types/oss'
import type { Result } from '@/types/api'

export const uploadFile = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, Result<any>>('/oss/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const getFileList = (params?: OssFileQuery) => {
  return request.get<any, Result<any[]>>('/oss/list', { params })
}

export const deleteFile = (id: number) => {
  return request.delete<any, Result<void>>(`/oss/${id}`)
}
