import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    showSidebar?: boolean = false
    headerInfo?: string
    headerClass?: string
  }
}