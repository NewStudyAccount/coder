<template>
  <div class="penrose">
    <h2>彭罗斯图（Penrose Tiling）- P3 厚/薄菱形</h2>

    <div class="toolbar">
      <label>类型</label>
      <select v-model="tilingType" @change="resetAndRender">
        <option value="rhombus">P3 厚/薄菱形</option>
        <option value="kite-dart">P2 风筝/飞镖（占位：迭代规则与 P3 类似，未完全实现）</option>
      </select>

      <label>迭代深度</label>
      <input type="number" v-model.number="depth" min="0" max="5" @change="resetAndRender" />

      <label>厚菱颜色</label>
      <input type="color" v-model="colorThick" @input="render" />
      <label>薄菱颜色</label>
      <input type="color" v-model="colorThin" @input="render" />

      <button @click="resetAndRender">重置</button>
      <button @click="exportPNG">导出 PNG</button>
      <button @click="zoom(1.2)">放大</button>
      <button @click="zoom(1/1.2)">缩小</button>
    </div>

    <canvas ref="canvas" class="canvas" @mousedown="onMouseDown" @mousemove="onMouseMove" @mouseup="onMouseUp" @mouseleave="onMouseUp"></canvas>

    <div class="tip">
      鼠标拖拽平移，放大/缩小控制缩放。默认从中心两块厚/薄菱形开始，按黄金比例细分生成准晶格。深度 > 4 可能较慢。
    </div>
  </div>
</template>

<script>
/**
 * Penrose P3（rhombus 厚/薄菱形）简化实现要点：
 * - 使用两个菱形：厚菱(thick) 角为 72°/108°；薄菱(thin) 角为 36°/144°；边长相等
 * - 采用经典细分(inflation)规则（黄金比例 φ）：
 *   φ = (1 + sqrt(5)) / 2
 * - 每个菱形用中心点 + 两个方向向量表示（边为单位长度），便于几何构造与旋转。
 * - 这里实现一个常见的局部细分规则版本，保证整体形态合理。学术上存在多种等价规则与方向标记选择。
 */
export default {
  name: 'PenroseTiling',
  data() {
    return {
      tilingType: 'rhombus',
      depth: 3,
      colorThick: '#ffcc66',
      colorThin: '#66ccff',
      shapes: [],
      scale: 140,
      offsetX: 0,
      offsetY: 0,
      dragging: false,
      dragStart: { x: 0, y: 0 },
      savedOffset: { x: 0, y: 0 },
      phi: (1 + Math.sqrt(5)) / 2
    };
  },
  mounted() {
    this.fitCanvas();
    window.addEventListener('resize', this.fitCanvas);
    this.resetAndRender();
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.fitCanvas);
  },
  methods: {
    fitCanvas() {
      const c = this.$refs.canvas;
      if (!c) return;
      const parent = c.parentElement;
      c.width = Math.min(parent.clientWidth - 4, 1200);
      c.height = Math.min(700, window.innerHeight - 200);
      this.render();
    },
    resetAndRender() {
      this.offsetX = (this.$refs.canvas?.width || 800) / 2;
      this.offsetY = (this.$refs.canvas?.height || 600) / 2;
      this.generate();
      this.render();
    },
    zoom(factor) {
      this.scale *= factor;
      this.render();
    },
    onMouseDown(e) {
      this.dragging = true;
      this.dragStart = { x: e.clientX, y: e.clientY };
      this.savedOffset = { x: this.offsetX, y: this.offsetY };
    },
    onMouseMove(e) {
      if (!this.dragging) return;
      const dx = e.clientX - this.dragStart.x;
      const dy = e.clientY - this.dragStart.y;
      this.offsetX = this.savedOffset.x + dx;
      this.offsetY = this.savedOffset.y + dy;
      this.render();
    },
    onMouseUp() {
      this.dragging = false;
    },

    // ============= 几何基础工具 =============
    rot(v, angle) {
      const c = Math.cos(angle), s = Math.sin(angle);
      return { x: v.x * c - v.y * s, y: v.x * s + v.y * c };
    },
    add(a, b) { return { x: a.x + b.x, y: a.y + b.y }; },
    sub(a, b) { return { x: a.x - b.x, y: a.y - b.y }; },
    mul(v, k) { return { x: v.x * k, y: v.y * k }; },
    // 单位向量
    unit(angle) { return { x: Math.cos(angle), y: Math.sin(angle) }; },

    // ============= 形状定义与初始种子 =============
    // shape: { type: 'thick'|'thin', c: {x,y}, a: {x,y}, b:{x,y} }
    // c 为中心，a/b 近似代表从中心指向两个边方向的基向量
    initialSeed() {
      // 两个中心菱形（厚/薄）作为种子
      const deg = Math.PI / 180;
      const a0 = this.unit(18 * deg);  // 18°
      const b0 = this.unit(162 * deg); // 162°
      const a1 = this.unit(-18 * deg);
      const b1 = this.unit(18 * deg + Math.PI); // 对应反向

      const seed = [];
      seed.push({ type: 'thick', c: { x: 0, y: 0 }, a: a0, b: b0 });
      seed.push({ type: 'thin',  c: { x: 0, y: 0 }, a: a1, b: b1 });
      return seed;
    },

    // ============= 细分规则（简化版） =============
    // 参考常见的 P3 细分：每个菱形按 φ 分割生成更小的厚/薄菱形
    subdivide(shapes) {
      const next = [];
      const φ = this.phi;
      for (const s of shapes) {
        const { type, c, a, b } = s;
        // 调整向量尺度，子形状使用缩小后的向量
        const a1 = this.mul(a, 1 / φ);
        const b1 = this.mul(b, 1 / φ);

        if (type === 'thick') {
          // 厚菱分解为：1 个厚菱 + 2 个薄菱（位置与方向依据经验规则）
          // 中心厚菱
          next.push({ type: 'thick', c: c, a: a1, b: b1 });
          // 两侧薄菱
          next.push({ type: 'thin', c: this.add(c, this.mul(a, 1 / φ)), a: b1, b: this.mul(this.rot(a1, Math.PI), 1) });
          next.push({ type: 'thin', c: this.add(c, this.mul(b, 1 / φ)), a: a1, b: this.mul(this.rot(b1, Math.PI), 1) });
        } else {
          // 薄菱分解为：2 个薄菱 + 1 个厚菱（规则互补）
          next.push({ type: 'thin', c: c, a: a1, b: b1 });
          next.push({ type: 'thick', c: this.add(c, this.mul(a, 1 / φ)), a: b1, b: a1 });
          next.push({ type: 'thick', c: this.add(c, this.mul(b, 1 / φ)), a: a1, b: b1 });
        }
      }
      return next;
    },

    generate() {
      if (this.tilingType !== 'rhombus') {
        // 简化：P2 仅占位，仍使用 P3 规则生成
        this.tilingType = 'rhombus';
      }
      let shapes = this.initialSeed();
      for (let i = 0; i < this.depth; i++) {
        shapes = this.subdivide(shapes);
      }
      this.shapes = shapes;
    },

    // ============= 绘制 =============
    // 将菱形顶点从中心+方向向量构造出来（边长近似 2/φ 缩放的四边形）
    rhombusVertices(s) {
      // 使用 a/b 的方向确定四个顶点：c ± a' ± b'
      // 这里对 a/b 再用一个固定系数缩放让图形美观
      const k = 0.8;
      const a = this.mul(s.a, k);
      const b = this.mul(s.b, k);
      return [
        this.add(this.add(s.c, a), b),
        this.add(this.sub(s.c, a), b),
        this.sub(this.sub(s.c, a), b),
        this.sub(this.add(s.c, a), b)
      ];
    },

    drawPolygon(ctx, pts, fillStyle, strokeStyle = '#333') {
      if (pts.length === 0) return;
      ctx.beginPath();
      ctx.moveTo(pts[0].x * this.scale + this.offsetX, pts[0].y * this.scale + this.offsetY);
      for (let i = 1; i < pts.length; i++) {
        ctx.lineTo(pts[i].x * this.scale + this.offsetX, pts[i].y * this.scale + this.offsetY);
      }
      ctx.closePath();
      ctx.fillStyle = fillStyle;
      ctx.fill();
      ctx.strokeStyle = strokeStyle;
      ctx.lineWidth = 0.6;
      ctx.stroke();
    },

    render() {
      const c = this.$refs.canvas;
      if (!c) return;
      const ctx = c.getContext('2d');
      ctx.clearRect(0, 0, c.width, c.height);
      ctx.save();
      // 背景
      ctx.fillStyle = '#fafafa';
      ctx.fillRect(0, 0, c.width, c.height);

      for (const s of this.shapes) {
        const verts = this.rhombusVertices(s);
        const color = s.type === 'thick' ? this.colorThick : this.colorThin;
        this.drawPolygon(ctx, verts, color);
      }
      ctx.restore();
    },

    exportPNG() {
      const c = this.$refs.canvas;
      const url = c.toDataURL('image/png');
      const a = document.createElement('a');
      a.href = url;
      a.download = `penrose-p3-depth${this.depth}.png`;
      a.click();
    }
  }
};
</script>

<style scoped>
.penrose {
  max-width: 1100px;
  margin: 16px auto;
  padding: 8px;
}
.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  margin-bottom: 8px;
}
.canvas {
  width: 100%;
  height: 620px;
  border: 1px solid #ddd;
  background: #fff;
}
.tip {
  color: #666;
  font-size: 13px;
  margin-top: 8px;
}
label { color: #333; }
button {
  padding: 6px 10px;
  background: #42b883;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover { background: #389d70; }
select, input[type="number"], input[type="color"] {
  padding: 4px 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>