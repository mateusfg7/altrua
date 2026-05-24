import { Button } from "~/components/ui/button";
import { Field, FieldGroup, FieldLabel } from "~/components/ui/field";
import { Input } from "~/components/ui/input";
import { cn } from "~/lib/utils";

export function LoginForm({
  className,
  ...props
}: React.ComponentProps<"form">) {
  return (
    <form className={cn("flex flex-col gap-6", className)} {...props}>
      <FieldGroup>
        <div className="flex flex-col items-center gap-1 text-center">
          <h1 className="font-bold text-2xl">Faça login na sua conta</h1>
          <p className="text-balance text-muted-foreground text-sm">
            Digite seu e-mail abaixo para fazer login
          </p>
        </div>
        <Field>
          <FieldLabel htmlFor="email">Email</FieldLabel>
          <Input
            className="bg-background"
            id="email"
            placeholder="eu@exemplo.com"
            required
            type="email"
          />
        </Field>
        <Field>
          <div className="flex items-center">
            <FieldLabel htmlFor="password">Senha</FieldLabel>
            <a
              className="ml-auto text-sm underline-offset-4 hover:underline"
              href="#"
            >
              Esqueceu sua senha?
            </a>
          </div>
          <Input
            className="bg-background"
            id="password"
            required
            type="password"
          />
        </Field>
        <Field>
          <Button type="submit">Login</Button>
        </Field>
      </FieldGroup>
    </form>
  );
}
