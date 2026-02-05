import request from '@/utils/request'

/**
 * 成绩管理相关API
 */

// 获取成绩列表
export function getGradeList(params) {
  return request({
    url: '/grades',
    method: 'get',
    params
  })
}

// 获取成绩详情
export function getGradeById(id) {
  return request({
    url: `/grades/${id}`,
    method: 'get'
  })
}

// 添加成绩
export function addGrade(data) {
  return request({
    url: '/grades',
    method: 'post',
    data
  })
}

// 更新成绩
export function updateGrade(id, data) {
  return request({
    url: `/grades/${id}`,
    method: 'put',
    data
  })
}

// 删除成绩
export function deleteGrade(id) {
  return request({
    url: `/grades/${id}`,
    method: 'delete'
  })
}

// 获取学生的所有成绩
export function getStudentGrades(studentId) {
  return request({
    url: `/grades/student/${studentId}`,
    method: 'get'
  })
}

// 获取课程的所有成绩
export function getCourseGrades(courseId, params) {
  return request({
    url: `/grades/course/${courseId}`,
    method: 'get',
    params
  })
}

// 批量录入成绩
export function batchAddGrades(data) {
  return request({
    url: '/grades/batch',
    method: 'post',
    data
  })
}

// 导出成绩
export function exportGrades(params) {
  return request({
    url: '/grades/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 成绩统计分析
export function getGradeStatistics(params) {
  return request({
    url: '/grades/statistics',
    method: 'get',
    params
  })
}
