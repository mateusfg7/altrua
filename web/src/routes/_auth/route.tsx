import { Outlet } from '@tanstack/react-router'
import { createFileRoute } from '@tanstack/react-router'
import { Footer } from '~/components/shell/footer'
import { Header } from '~/components/shell/header'

export const Route = createFileRoute('/_auth')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div className="flex min-h-dvh flex-col gap-20">
              <Header />
              <main className="flex-1">
                <Outlet />
              </main>
              <Footer />
            </div>
}
