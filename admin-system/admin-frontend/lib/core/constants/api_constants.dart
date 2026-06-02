class ApiConstants {
  static const String baseUrl = 'http://localhost:8080/api';
  static const String login = '/auth/login';
  static const String logout = '/auth/logout';
  static const String refreshToken = '/auth/refresh';
  static const String userInfo = '/auth/info';

  static const String userList = '/system/user/list';
  static const String userDetail = '/system/user';
  static const String roleList = '/system/role/list';
  static const String roleAll = '/system/role/all';
  static const String roleDetail = '/system/role';
  static const String roleMenuIds = '/system/role/menuIds';
  static const String roleAssignMenu = '/system/role/assignMenu';
  static const String menuTree = '/system/menu/tree';
  static const String menuList = '/system/menu/list';
  static const String menuDetail = '/system/menu';
  static const String deptList = '/system/dept/list';
  static const String deptTree = '/system/dept/tree';
  static const String deptDetail = '/system/dept';
  static const String postList = '/system/post/list';
  static const String postAll = '/system/post/all';
  static const String dictTypeList = '/system/dict/type/list';
  static const String dictDataList = '/system/dict/data/list';
  static const String dictDataByType = '/system/dict/data/type';
  static const String configList = '/system/config/list';
  static const String noticeList = '/system/notice/list';
  static const String operLogList = '/monitor/operlog/list';
  static const String loginLogList = '/monitor/loginlog/list';
}
