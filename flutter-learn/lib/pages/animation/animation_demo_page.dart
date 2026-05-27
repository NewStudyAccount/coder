import 'package:flutter/material.dart';

class AnimationDemoPage extends StatelessWidget {
  const AnimationDemoPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('动画效果')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _SectionCard(
            title: '隐式动画 (Implicit)',
            subtitle: '只需设置目标值，Flutter 自动处理过渡',
            child: const _ImplicitAnimationDemo(),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'AnimatedContainer',
            subtitle: '容器属性变化动画',
            child: const _AnimatedContainerDemo(),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'Hero 动画',
            subtitle: '页面间共享元素过渡',
            child: _HeroDemo(),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: '交错动画 (Staggered)',
            subtitle: '多个动画按顺序执行',
            child: const _StaggeredAnimationDemo(),
          ),
        ],
      ),
    );
  }
}

class _SectionCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final Widget child;

  const _SectionCard({
    required this.title,
    required this.subtitle,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: Theme.of(context).colorScheme.primary,
                  ),
            ),
            const SizedBox(height: 4),
            Text(
              subtitle,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Theme.of(context).disabledColor,
                  ),
            ),
            const Divider(height: 24),
            child,
          ],
        ),
      ),
    );
  }
}

class _ImplicitAnimationDemo extends StatefulWidget {
  const _ImplicitAnimationDemo();

  @override
  State<_ImplicitAnimationDemo> createState() => _ImplicitAnimationDemoState();
}

class _ImplicitAnimationDemoState extends State<_ImplicitAnimationDemo> {
  bool _expanded = false;
  double _opacity = 1.0;
  double _rotation = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            AnimatedOpacity(
              opacity: _opacity,
              duration: const Duration(milliseconds: 500),
              child: Container(
                width: 60,
                height: 60,
                color: Colors.blue,
                child: const Center(
                  child: Text('透明', style: TextStyle(color: Colors.white)),
                ),
              ),
            ),
            AnimatedRotation(
              turns: _rotation,
              duration: const Duration(milliseconds: 500),
              child: Container(
                width: 60,
                height: 60,
                color: Colors.green,
                child: const Center(
                  child: Text('旋转', style: TextStyle(color: Colors.white)),
                ),
              ),
            ),
            AnimatedScale(
              scale: _expanded ? 1.5 : 1.0,
              duration: const Duration(milliseconds: 500),
              child: Container(
                width: 60,
                height: 60,
                color: Colors.orange,
                child: const Center(
                  child: Text('缩放', style: TextStyle(color: Colors.white)),
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        FilledButton(
          onPressed: () {
            setState(() {
              _expanded = !_expanded;
              _opacity = _opacity == 1.0 ? 0.3 : 1.0;
              _rotation += 0.25;
            });
          },
          child: const Text('播放动画'),
        ),
      ],
    );
  }
}

class _AnimatedContainerDemo extends StatefulWidget {
  const _AnimatedContainerDemo();

  @override
  State<_AnimatedContainerDemo> createState() => _AnimatedContainerDemoState();
}

class _AnimatedContainerDemoState extends State<_AnimatedContainerDemo> {
  bool _changed = false;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Center(
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 600),
            curve: Curves.easeInOutCubic,
            width: _changed ? 200 : 100,
            height: _changed ? 100 : 200,
            decoration: BoxDecoration(
              color: _changed
                  ? Theme.of(context).colorScheme.primary
                  : Colors.orange,
              borderRadius: BorderRadius.circular(_changed ? 50 : 12),
            ),
            child: Center(
              child: Text(
                _changed ? '宽' : '高',
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
        ),
        const SizedBox(height: 16),
        FilledButton(
          onPressed: () => setState(() => _changed = !_changed),
          child: const Text('切换形态'),
        ),
      ],
    );
  }
}

class _HeroDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        Navigator.push(
          context,
          PageRouteBuilder(
            pageBuilder: (_, _, _) => const _HeroDetailPage(),
            transitionDuration: const Duration(milliseconds: 500),
          ),
        );
      },
      child: Hero(
        tag: 'hero-demo',
        child: Container(
          width: 100,
          height: 100,
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.primary,
            borderRadius: BorderRadius.circular(16),
          ),
          child: const Center(
            child: Text('点击我', style: TextStyle(color: Colors.white)),
          ),
        ),
      ),
    );
  }
}

class _HeroDetailPage extends StatelessWidget {
  const _HeroDetailPage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Hero 详情页')),
      body: Center(
        child: Hero(
          tag: 'hero-demo',
          child: Container(
            width: 250,
            height: 250,
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.primary,
              borderRadius: BorderRadius.circular(32),
            ),
            child: const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.star, size: 64, color: Colors.white),
                  SizedBox(height: 16),
                  Text(
                    'Hero 动画过渡',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _StaggeredAnimationDemo extends StatefulWidget {
  const _StaggeredAnimationDemo();

  @override
  State<_StaggeredAnimationDemo> createState() =>
      _StaggeredAnimationDemoState();
}

class _StaggeredAnimationDemoState extends State<_StaggeredAnimationDemo>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _fadeIn;
  late Animation<double> _slideUp;
  late Animation<double> _scaleUp;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    );

    _fadeIn = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.0, 0.4, curve: Curves.easeIn),
      ),
    );

    _slideUp = Tween<double>(begin: 50, end: 0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.2, 0.6, curve: Curves.easeOut),
      ),
    );

    _scaleUp = Tween<double>(begin: 0.5, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.4, 1.0, curve: Curves.elasticOut),
      ),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AnimatedBuilder(
          animation: _controller,
          builder: (context, child) {
            return Opacity(
              opacity: _fadeIn.value,
              child: Transform.translate(
                offset: Offset(0, _slideUp.value),
                child: Transform.scale(
                  scale: _scaleUp.value,
                  child: child,
                ),
              ),
            );
          },
          child: Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  Theme.of(context).colorScheme.primary,
                  Theme.of(context).colorScheme.secondary,
                ],
              ),
              borderRadius: BorderRadius.circular(16),
            ),
            child: const Text(
              '交错动画效果',
              style: TextStyle(
                color: Colors.white,
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
        const SizedBox(height: 16),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            FilledButton(
              onPressed: () => _controller.forward(from: 0),
              child: const Text('播放'),
            ),
            const SizedBox(width: 12),
            OutlinedButton(
              onPressed: () => _controller.reverse(),
              child: const Text('反向'),
            ),
          ],
        ),
      ],
    );
  }
}
