import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'app/routes/app_routes.dart';
import 'app/theme/app_theme.dart';
import 'app/state/counter_provider.dart';
import 'app/state/todo_provider.dart';
import 'app/state/theme_provider.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => CounterProvider()),
        ChangeNotifierProvider(create: (_) => TodoProvider()),
        ChangeNotifierProvider(create: (_) => ThemeProvider()),
      ],
      child: const FlutterLearnApp(),
    ),
  );
}

class FlutterLearnApp extends StatelessWidget {
  const FlutterLearnApp({super.key});

  @override
  Widget build(BuildContext context) {
    final themeProvider = Provider.of<ThemeProvider>(context);
    return MaterialApp(
      title: 'Flutter 学习',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: themeProvider.themeMode,
      initialRoute: AppRoutes.home,
      routes: AppRoutes.routes,
    );
  }
}
