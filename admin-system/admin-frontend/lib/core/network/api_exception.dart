class ApiException implements Exception {
  final int? code;
  final String message;

  ApiException({this.code, required this.message});

  factory ApiException.unauthorized() =>
      ApiException(code: 401, message: '认证失败，请重新登录');

  factory ApiException.forbidden() =>
      ApiException(code: 403, message: '没有权限访问该资源');

  factory ApiException.serverError() =>
      ApiException(code: 500, message: '服务器内部错误');

  factory ApiException.networkError() =>
      ApiException(code: -1, message: '网络连接失败');

  @override
  String toString() => 'ApiException(code: $code, message: $message)';
}
