import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../features/auth/presentation/pages/login_page.dart';
import '../../features/dashboard/presentation/pages/dashboard_page.dart';
import '../../features/system/user/presentation/pages/user_list_page.dart';
import '../../features/system/role/presentation/pages/role_list_page.dart';
import '../../features/system/menu/presentation/pages/menu_list_page.dart';
import '../../features/system/dept/presentation/pages/dept_list_page.dart';
import '../../features/system/post/presentation/pages/post_list_page.dart';
import '../../features/system/dict/presentation/pages/dict_type_list_page.dart';
import '../../features/system/config/presentation/pages/config_list_page.dart';
import '../../features/system/notice/presentation/pages/notice_list_page.dart';
import '../../features/monitor/operlog/presentation/pages/operlog_list_page.dart';
import '../../features/monitor/loginlog/presentation/pages/loginlog_list_page.dart';
import '../../features/monitor/online/presentation/pages/online_user_list_page.dart';
import '../../features/monitor/server/presentation/pages/server_monitor_page.dart';
import '../../features/devtools/gen/presentation/pages/gen_table_list_page.dart';
import '../../features/devtools/job/presentation/pages/job_list_page.dart';
import '../../features/devtools/job_log/presentation/pages/job_log_list_page.dart';
import '../storage/storage_service.dart';
import '../../shared/layouts/admin_layout.dart';

final routerProvider = Provider<GoRouter>((ref) {
  final storage = ref.watch(storageServiceProvider);
  return GoRouter(
    initialLocation: '/login',
    redirect: (context, state) {
      final isLoggedIn = storage.isLoggedIn;
      final isLoginRoute = state.matchedLocation == '/login';

      if (!isLoggedIn && !isLoginRoute) return '/login';
      if (isLoggedIn && isLoginRoute) return '/dashboard';
      return null;
    },
    routes: [
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginPage(),
      ),
      ShellRoute(
        builder: (context, state, child) => AdminLayout(child: child),
        routes: [
          GoRoute(
            path: '/dashboard',
            builder: (context, state) => const DashboardPage(),
          ),
          GoRoute(
            path: '/system/user',
            builder: (context, state) => const UserListPage(),
          ),
          GoRoute(
            path: '/system/role',
            builder: (context, state) => const RoleListPage(),
          ),
          GoRoute(
            path: '/system/menu',
            builder: (context, state) => const MenuListPage(),
          ),
          GoRoute(
            path: '/system/dept',
            builder: (context, state) => const DeptListPage(),
          ),
          GoRoute(
            path: '/system/post',
            builder: (context, state) => const PostListPage(),
          ),
          GoRoute(
            path: '/system/dict',
            builder: (context, state) => const DictTypeListPage(),
          ),
          GoRoute(
            path: '/system/config',
            builder: (context, state) => const ConfigListPage(),
          ),
          GoRoute(
            path: '/system/notice',
            builder: (context, state) => const NoticeListPage(),
          ),
          GoRoute(
            path: '/monitor/operlog',
            builder: (context, state) => const OperLogListPage(),
          ),
          GoRoute(
            path: '/monitor/loginlog',
            builder: (context, state) => const LoginLogListPage(),
          ),
          GoRoute(
            path: '/monitor/online',
            builder: (context, state) => const OnlineUserListPage(),
          ),
          GoRoute(
            path: '/monitor/server',
            builder: (context, state) => const ServerMonitorPage(),
          ),
          GoRoute(
            path: '/devtools/gen',
            builder: (context, state) => const GenTableListPage(),
          ),
          GoRoute(
            path: '/devtools/job',
            builder: (context, state) => const JobListPage(),
          ),
          GoRoute(
            path: '/devtools/job-log',
            builder: (context, state) => const JobLogListPage(),
          ),
        ],
      ),
    ],
  );
});
