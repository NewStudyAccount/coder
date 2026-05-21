export interface Product {
  id: number
  name: string
  categoryId: number
  mainImage: string
  images: string
  description: string
  price: number
  originalPrice: number
  stock: number
  sales: number
  status: number
  sort: number
  createTime: string
  updateTime: string
  categoryName: string
  skuList: ProductSku[]
}

export interface ProductSku {
  id?: number
  productId?: number
  name: string
  specs: string
  price: number
  originalPrice: number
  stock: number
  image: string
  status: number
}

export interface ProductForm {
  id?: number
  name: string
  categoryId?: number
  mainImage?: string
  images?: string
  description?: string
  price: number
  originalPrice?: number
  stock?: number
  sales?: number
  status?: number
  sort?: number
  skuList?: ProductSku[]
}

export interface ProductQuery {
  name?: string
  categoryId?: number
  status?: number
}
