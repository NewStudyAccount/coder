import 'package:flutter/material.dart';
import '../../pages/home/home_page.dart';
import '../../pages/basics/widget_catalog_page.dart';
import '../../pages/basics/layout_demo_page.dart';
import '../../pages/state/counter_page.dart';
import '../../pages/state/todo_page.dart';
import '../../pages/state/provider_demo_page.dart';
import '../../pages/network/network_demo_page.dart';
import '../../pages/storage/storage_demo_page.dart';
import '../../pages/animation/animation_demo_page.dart';
import '../../pages/navigation/navigation_demo_page.dart';
import '../../pages/navigation/second_page.dart';
import '../../pages/navigation/third_page.dart';

class AppRoutes {
  static const String home = '/';
  static const String widgetCatalog = '/basics/widgets';
  static const String layoutDemo = '/basics/layout';
  static const String counter = '/state/counter';
  static const String todo = '/state/todo';
  static const String providerDemo = '/state/provider';
  static const String networkDemo = '/network';
  static const String storageDemo = '/storage';
  static const String animationDemo = '/animation';
  static const String navigationDemo = '/navigation';
  static const String secondPage = '/navigation/second';
  static const String thirdPage = '/navigation/third';

  static Map<String, WidgetBuilder> get routes => {
        home: (_) => const HomePage(),
        widgetCatalog: (_) => const WidgetCatalogPage(),
        layoutDemo: (_) => const LayoutDemoPage(),
        counter: (_) => const CounterPage(),
        todo: (_) => const TodoPage(),
        providerDemo: (_) => const ProviderDemoPage(),
        networkDemo: (_) => const NetworkDemoPage(),
        storageDemo: (_) => const StorageDemoPage(),
        animationDemo: (_) => const AnimationDemoPage(),
        navigationDemo: (_) => const NavigationDemoPage(),
        secondPage: (_) => const SecondPage(),
        thirdPage: (_) => const ThirdPage(),
      };
}
