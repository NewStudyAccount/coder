class LoginRequest {
  final String username;
  final String password;

  LoginRequest({required this.username, required this.password});

  Map<String, dynamic> toJson() => {
        'username': username,
        'password': password,
      };
}

class LoginResponse {
  final String accessToken;
  final String refreshToken;
  final int expiresIn;

  LoginResponse({
    required this.accessToken,
    required this.refreshToken,
    required this.expiresIn,
  });

  factory LoginResponse.fromJson(Map<String, dynamic> json) => LoginResponse(
        accessToken: json['data']['accessToken'] ?? '',
        refreshToken: json['data']['refreshToken'] ?? '',
        expiresIn: json['data']['expiresIn'] ?? 0,
      );
}

class UserInfo {
  final int? userId;
  final String? username;
  final String? nickname;
  final int? deptId;
  final Set<String>? permissions;
  final List<MenuVO>? menus;

  UserInfo({
    this.userId,
    this.username,
    this.nickname,
    this.deptId,
    this.permissions,
    this.menus,
  });

  factory UserInfo.fromJson(Map<String, dynamic> json) => UserInfo(
        userId: json['user']?['userId'],
        username: json['user']?['username'],
        nickname: json['user']?['nickname'],
        deptId: json['user']?['deptId'],
        permissions: (json['permissions'] as List?)?.map((e) => e.toString()).toSet(),
        menus: (json['menus'] as List?)?.map((e) => MenuVO.fromJson(e)).toList(),
      );
}

class MenuVO {
  final int? id;
  final String? menuName;
  final int? parentId;
  final String? path;
  final String? component;
  final int? menuType;
  final String? perms;
  final String? icon;
  final int? sort;
  final List<MenuVO>? children;

  MenuVO({
    this.id,
    this.menuName,
    this.parentId,
    this.path,
    this.component,
    this.menuType,
    this.perms,
    this.icon,
    this.sort,
    this.children,
  });

  factory MenuVO.fromJson(Map<String, dynamic> json) => MenuVO(
        id: json['id'],
        menuName: json['menuName'],
        parentId: json['parentId'],
        path: json['path'],
        component: json['component'],
        menuType: json['menuType'],
        perms: json['perms'],
        icon: json['icon'],
        sort: json['sort'],
        children: (json['children'] as List?)?.map((e) => MenuVO.fromJson(e)).toList(),
      );
}
