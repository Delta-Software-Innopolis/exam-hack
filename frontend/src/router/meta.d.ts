import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    showSidebar: boolean = true
    headerInfo?: string
    headerClass?: string
  }
}