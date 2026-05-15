import { FavouriteIcon } from '@hugeicons/core-free-icons'
import { HugeiconsIcon } from '@hugeicons/react'
import { createFileRoute, Link } from '@tanstack/react-router'
import { LoginForm } from '~/components/login-form'

import illustration from "~/assets/shane-rounce-DNkoNXQti3c-unsplash.jpg"

export const Route = createFileRoute('/sign-in')({
  component: RouteComponent,
})

function RouteComponent() {
  return (
  <div className="grid min-h-svh lg:grid-cols-2">
      <div className="flex flex-col gap-4 p-6 md:p-10">
        <div className="flex justify-center gap-2 md:justify-start">
           <Link className="flex items-center gap-2" to="/">
                    <div className="flex size-9 items-center justify-center rounded-lg bg-primary">
                      <HugeiconsIcon
                        className="size-5 text-primary-foreground"
                        icon={FavouriteIcon}
                      />
                    </div>
                    <span className="font-bold text-xl tracking-tight">Altrua</span>
                  </Link>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-xs">
            <LoginForm />
          </div>
        </div>
      </div>
      <div className="relative hidden bg-muted lg:block">
        <img
          src={illustration}
          alt="Image"
          className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"
        />
      </div>
    </div>)
}
