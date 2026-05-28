class User {
  final int id;
  final String username;
  final String? email;
  final String? avatar;
  final String? phone;
  final List<String> roles;

  const User({
    required this.id,
    required this.username,
    this.email,
    this.avatar,
    this.phone,
    this.roles = const [],
  });
}
