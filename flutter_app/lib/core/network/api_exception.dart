class ApiException implements Exception {
  final int code;
  final String message;
  final dynamic data;

  ApiException({
    required this.code,
    required this.message,
    this.data,
  });

  @override
  String toString() => 'ApiException(code: $code, message: $message)';
}
