import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

class NetworkDemoPage extends StatefulWidget {
  const NetworkDemoPage({super.key});

  @override
  State<NetworkDemoPage> createState() => _NetworkDemoPageState();
}

class _NetworkDemoPageState extends State<NetworkDemoPage> {
  final Dio _dio = Dio(BaseOptions(
    baseUrl: 'https://jsonplaceholder.typicode.com',
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 10),
  ));

  bool _isLoading = false;
  List<User> _users = [];
  List<Post> _posts = [];
  String _errorMessage = '';
  int _currentTab = 0;

  @override
  void initState() {
    super.initState();
    _fetchUsers();
  }

  Future<void> _fetchUsers() async {
    setState(() {
      _isLoading = true;
      _errorMessage = '';
    });
    try {
      final response = await _dio.get('/users');
      final users = (response.data as List)
          .map((json) => User.fromJson(json))
          .toList();
      setState(() {
        _users = users;
        _isLoading = false;
      });
    } on DioException catch (e) {
      setState(() {
        _errorMessage = '请求失败: ${e.message}';
        _isLoading = false;
      });
    }
  }

  Future<void> _fetchPosts() async {
    setState(() {
      _isLoading = true;
      _errorMessage = '';
    });
    try {
      final response = await _dio.get('/posts?_limit=10');
      final posts = (response.data as List)
          .map((json) => Post.fromJson(json))
          .toList();
      setState(() {
        _posts = posts;
        _isLoading = false;
      });
    } on DioException catch (e) {
      setState(() {
        _errorMessage = '请求失败: ${e.message}';
        _isLoading = false;
      });
    }
  }

  Future<void> _createPost() async {
    try {
      final response = await _dio.post('/posts', data: {
        'title': 'Flutter 学习笔记',
        'body': '通过 Dio 发送 POST 请求',
        'userId': 1,
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('创建成功! ID: ${response.data['id']}')),
        );
      }
    } on DioException catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('创建失败: ${e.message}')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('网络请求 - Dio')),
      body: Column(
        children: [
          TabBar(
            tabs: const [
              Tab(text: 'GET 用户列表'),
              Tab(text: 'GET 文章列表'),
              Tab(text: 'POST 创建'),
            ],
            onTap: (index) {
              setState(() => _currentTab = index);
              if (index == 0) _fetchUsers();
              if (index == 1) _fetchPosts();
            },
          ),
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _errorMessage.isNotEmpty
                    ? _ErrorWidget(
                        message: _errorMessage,
                        onRetry: _currentTab == 0 ? _fetchUsers : _fetchPosts,
                      )
                    : _buildContent(),
          ),
        ],
      ),
    );
  }

  Widget _buildContent() {
    switch (_currentTab) {
      case 0:
        return _buildUserList();
      case 1:
        return _buildPostList();
      case 2:
        return _buildPostDemo();
      default:
        return const SizedBox();
    }
  }

  Widget _buildUserList() {
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _users.length,
      itemBuilder: (context, index) {
        final user = _users[index];
        return Card(
          child: ListTile(
            leading: CircleAvatar(
              backgroundColor:
                  Colors.primaries[index % Colors.primaries.length],
              child: Text(
                user.name.substring(0, 1),
                style: const TextStyle(color: Colors.white),
              ),
            ),
            title: Text(user.name),
            subtitle: Text(user.email),
            trailing: Text('@${user.username}',
                style: Theme.of(context).textTheme.bodySmall),
          ),
        );
      },
    );
  }

  Widget _buildPostList() {
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _posts.length,
      itemBuilder: (context, index) {
        final post = _posts[index];
        return Card(
          margin: const EdgeInsets.only(bottom: 8),
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Chip(
                      label: Text('ID: ${post.id}'),
                      visualDensity: VisualDensity.compact,
                    ),
                    const SizedBox(width: 8),
                    Text('UserId: ${post.userId}',
                        style: Theme.of(context).textTheme.bodySmall),
                  ],
                ),
                const SizedBox(height: 8),
                Text(
                  post.title,
                  style: const TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 15,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  post.body,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: TextStyle(
                    color: Theme.of(context).disabledColor,
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildPostDemo() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('POST 请求示例',
                      style: Theme.of(context).textTheme.titleMedium),
                  const SizedBox(height: 12),
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: Theme.of(context)
                          .colorScheme
                          .surfaceContainerHighest,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: const Text(
                      'POST /posts\n'
                      '{\n'
                      '  "title": "Flutter 学习笔记",\n'
                      '  "body": "通过 Dio 发送 POST 请求",\n'
                      '  "userId": 1\n'
                      '}',
                      style: TextStyle(fontFamily: 'monospace', fontSize: 13),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          FilledButton.icon(
            onPressed: _createPost,
            icon: const Icon(Icons.send),
            label: const Text('发送 POST 请求'),
          ),
          const SizedBox(height: 24),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('Dio 核心用法',
                      style: Theme.of(context).textTheme.titleMedium),
                  const SizedBox(height: 12),
                  const Text('1. 创建 Dio 实例，配置 baseUrl 和超时'),
                  const Text('2. GET 请求: dio.get("/path")'),
                  const Text('3. POST 请求: dio.post("/path", data: {...})'),
                  const Text('4. 拦截器: dio.interceptors.add(...)'),
                  const Text('5. 错误处理: DioException 捕获'),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _ErrorWidget extends StatelessWidget {
  final String message;
  final VoidCallback onRetry;

  const _ErrorWidget({required this.message, required this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, size: 64, color: Colors.red),
          const SizedBox(height: 16),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 32),
            child: Text(message, textAlign: TextAlign.center),
          ),
          const SizedBox(height: 16),
          FilledButton(onPressed: onRetry, child: const Text('重试')),
        ],
      ),
    );
  }
}

class User {
  final int id;
  final String name;
  final String username;
  final String email;

  User({
    required this.id,
    required this.name,
    required this.username,
    required this.email,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      name: json['name'],
      username: json['username'],
      email: json['email'],
    );
  }
}

class Post {
  final int id;
  final int userId;
  final String title;
  final String body;

  Post({
    required this.id,
    required this.userId,
    required this.title,
    required this.body,
  });

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      id: json['id'],
      userId: json['userId'],
      title: json['title'],
      body: json['body'],
    );
  }
}
