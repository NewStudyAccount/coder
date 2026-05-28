class ApiConstants {
  ApiConstants._();

  static const String baseUrl = 'http://localhost:8080/api';
  static const Duration connectTimeout = Duration(seconds: 15);
  static const Duration receiveTimeout = Duration(seconds: 15);

  // Auth
  static const String login = '/auth/login';
  static const String register = '/auth/register';
  static const String userinfo = '/auth/userinfo';
  static const String menus = '/auth/menus';
}
