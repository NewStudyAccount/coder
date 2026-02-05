import request from '@/utils/request'

/**
 * 学生管理相关API
 */

// 获取学生列表
export function getStudentList(params) {
  return request({
    url: '/students',
    method: 'get',
    params
  })
}

// 获取学生详情
export function getStudentById(id) {
  return request({
    url: `/students/${id}`,
    method: 'get'
  })
}

// 添加学生
export function addStudent(data) {
  return request({
    url: '/students',
    method: 'post',
    data
  })
}

// 更新学生信息
export function updateStudent(id, data) {
  return request({
    url: `/students/${id}`,
    method: 'put',
    data
  })
}

// 删除学生
export function deleteStudent(id) {
  return request({
    url: `/students/${id}`,
    method: 'delete'
  })
}

// 批量删除学生
export function batchDeleteStudents(ids) {
  return request({
    url: '/students/batch',
    method: 'delete',
    data: { ids }
  })
}

// 导出学生数据
export function exportStudents(params) {
  return request({
    url: '/students/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 搜索学生
export function searchStudents(keyword) {
  return request({
    url: '/students/search',
    method: 'get',
    params: { keyword }
  })
}
