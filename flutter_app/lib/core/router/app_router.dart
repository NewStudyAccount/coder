import 'package:go_router/go_router.dart';

import '../../features/auth/presentation/pages/login_page.dart';
import '../../features/auth/presentation/pages/register_page.dart';
import '../../features/home/presentation/pages/home_page.dart';
import '../../features/profile/presentation/pages/profile_page.dart';
import '../../shared/layouts/main_layout.dart';

abstract class AppRoutes {
  static const String login = '/auth/login';
  static const String register = '/auth/register';
  static const String home = '/home';
  static const String profile = '/profile';

  static final List<String> publicRoutes = [login, register];
}

GoRouter createAppRouter({
  required bool isAuthenticated,
  required void Function() onUnauthorized,
}) {
  return GoRouter(
    initialLocation: AppRoutes.home,
    redirect: (context, state) {
      final isPublic = AppRoutes.publicRoutes.contains(state.matchedLocation);
      if (!isAuthenticated && !isPublic) {
        return AppRoutes.login;
      }
      if (isAuthenticated && isPublic) {
        return AppRoutes.home;
      }
      return null;
    },
    routes: [
      GoRoute(
        path: AppRoutes.login,
        name: 'AuthLogin',
        builder: (context, state) => const LoginPage(),
      ),
      GoRoute(
        path: AppRoutes.register,
        name: 'AuthRegister',
        builder: (context, state) => const RegisterPage(),
      ),
      ShellRoute(
        builder: (context, state, child) => MainLayout(child: child),
        routes: [
          GoRoute(
            path: AppRoutes.home,
            name: 'Home',
            builder: (context, state) => const HomePage(),
          ),
          GoRoute(
            path: AppRoutes.profile,
            name: 'Profile',
            builder: (context, state) => const ProfilePage(),
          ),
        ],
      ),
    ],
  );
}
