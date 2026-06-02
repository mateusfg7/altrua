import { useForm } from "@tanstack/react-form";
import { Link, useRouter } from "@tanstack/react-router";
import { toast } from "sonner";
import z from "zod";
import { Button } from "~/components/ui/button";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "~/components/ui/field";
import { Input } from "~/components/ui/input";
import { useSignIn } from "~/hooks/use-sign-in";
import { cn } from "~/lib/utils";
import { useAuthStore } from "~/store/auth.store";

const schema = z.object({
  email: z.email("O email deve ser válido"),
  password: z.string("A senha é obrigatória").nonempty("A senha é obrigatória"),
});

export function SignInForm() {
  const setTokens = useAuthStore((s) => s.setTokens);
  const { mutateAsync } = useSignIn();

  const router = useRouter();

  const form = useForm({
    defaultValues: {
      email: "",
      password: "",
    } as z.infer<typeof schema>,
    validators: {
      onSubmit: schema,
    },
    onSubmit: async ({ value: { email, password } }) => {
      console.log("chegou aqui");

      await mutateAsync(
        { email, password },
        {
          onSuccess: (tokens) => {
            toast.success("Login realizado com sucesso! Redirecionando...");
            setTokens(tokens);
            router.navigate({ to: "/" });
          },
          onError: (error) => {
            toast.error(
              error.response?.data?.detail || "Ocorreu um erro ao fazer login"
            );
          },
        }
      );
    },
    onSubmitInvalid: () => {
      toast.error("Por favor, corrija os erros no formulário antes de enviar.");
    },
  });

  return (
    <form
      className={cn("flex flex-col gap-6")}
      onSubmit={(e) => {
        e.preventDefault();
        form.handleSubmit();
      }}
    >
      <div className="flex flex-col items-center gap-1 text-center">
        <h1 className="font-bold text-2xl">Faça login na sua conta</h1>
        <p className="text-balance text-muted-foreground text-sm">
          Digite seu e-mail abaixo para fazer login
        </p>
      </div>
      <FieldGroup>
        <form.Field name="email">
          {(field) => {
            const isInvalid =
              field.state.meta.isTouched && !field.state.meta.isValid;
            return (
              <Field data-invalid={isInvalid}>
                <FieldLabel htmlFor={field.name}>Email</FieldLabel>
                <Input
                  aria-invalid={isInvalid}
                  autoComplete="email"
                  id={field.name}
                  name={field.name}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                  placeholder="eu@exemplo.com"
                  type="email"
                  value={field.state.value}
                />
                {isInvalid && <FieldError errors={field.state.meta.errors} />}
              </Field>
            );
          }}
        </form.Field>

        <form.Field name="password">
          {(field) => {
            const isInvalid =
              field.state.meta.isTouched && !field.state.meta.isValid;
            return (
              <Field data-invalid={isInvalid}>
                <FieldLabel htmlFor={field.name}>Senha</FieldLabel>
                <Input
                  aria-invalid={isInvalid}
                  autoComplete="new-password"
                  id={field.name}
                  name={field.name}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                  type="password"
                  value={field.state.value}
                />
                {isInvalid && <FieldError errors={field.state.meta.errors} />}
              </Field>
            );
          }}
        </form.Field>

        <Field>
          <form.Subscribe selector={(s) => s.isSubmitting}>
            {(isSubmitting) => (
              <Button className="w-full" disabled={isSubmitting} type="submit">
                Login
              </Button>
            )}
          </form.Subscribe>
          <p className="text-center">
            Ainda não tem uma conta?{" "}
            <Button asChild variant="link">
              <Link to="/sign-up">Cadastre-se</Link>
            </Button>
          </p>
        </Field>
      </FieldGroup>
    </form>
  );
}
