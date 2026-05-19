export interface OssFileInfo {
  id: number
  originalName: string
  fileName: string
  filePath: string
  fileSize: number
  fileType: string
  url: string
  uploadUserId: number
  uploadUserName: string
  createTime: string
}

export interface OssFileQuery {
  originalName?: string
}
