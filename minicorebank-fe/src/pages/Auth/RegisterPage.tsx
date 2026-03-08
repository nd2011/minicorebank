import { useMemo, useState } from "react"
import { useNavigate } from "react-router-dom"
import { registerCustomer } from "@/services/auth"

export default function RegisterPage() {
  const year = useMemo(() => new Date().getFullYear(), [])
  const navigate = useNavigate()

  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [fullName, setFullName] = useState("")
  const [phone, setPhone] = useState("")
  const [email, setEmail] = useState("")
  const [currency, setCurrency] = useState("VND")

  const emailInvalid = email.length > 0 && !/^\S+@\S+\.\S+$/.test(email)
  const phoneInvalid = phone.length > 0 && !/^\+?\d{8,15}$/.test(phone.replace(/\s/g, ""))
  const usernameInvalid = username.length > 0 && username.length < 4
  const passwordInvalid = password.length > 0 && password.length < 6

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)

    if (!fullName.trim()) return setError("Vui lòng nhập họ tên.")
    if (usernameInvalid) return setError("Username tối thiểu 4 ký tự.")
    if (passwordInvalid) return setError("Mật khẩu tối thiểu 6 ký tự.")
    if (emailInvalid) return setError("Email không hợp lệ.")
    if (phoneInvalid) return setError("Số điện thoại không hợp lệ (8–15 số).")
    if (!currency) return setError("Vui lòng chọn currency.")

    try {
      setLoading(true)
      const res = await registerCustomer({
        username,
        password,
        fullName,
        phone: phone.replace(/\s/g, ""),
        email,
        currency,
      })
      console.log("Register res:", res)
      localStorage.setItem("token", res.accessToken)
      localStorage.setItem("role", res.role)
      navigate("/dashboard")
    } catch (err: any) {
      setError(err?.response?.data?.message ?? "Đăng ký thất bại. Vui lòng thử lại.")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="relative min-h-screen overflow-hidden bg-slate-950 text-slate-100">
      <div className="pointer-events-none absolute inset-0">
        <div className="absolute -left-44 -top-44 h-[540px] w-[540px] rounded-full bg-indigo-600/30 blur-3xl" />
        <div className="absolute -right-44 top-10 h-[540px] w-[540px] rounded-full bg-cyan-500/20 blur-3xl" />
        <div className="absolute bottom-[-240px] left-1/2 h-[540px] w-[540px] -translate-x-1/2 rounded-full bg-fuchsia-500/10 blur-3xl" />
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_1px_1px,rgba(255,255,255,0.06)_1px,transparent_0)] [background-size:18px_18px]" />
      </div>

      <div className="relative h-screen w-screen">
        <div className="grid h-screen w-screen grid-cols-1 md:grid-cols-2">
          {/* Left */}
          <div className="relative hidden md:block">
            <div className="absolute inset-0 bg-gradient-to-br from-indigo-500/25 via-white/0 to-cyan-400/10" />
            <div className="relative flex h-screen flex-col justify-between p-12">
              <div>
                <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/5 px-4 py-2 text-sm">
                  <span className="h-2 w-2 rounded-full bg-emerald-400" />
                  Core Banking • Register
                </div>

                <h1 className="mt-7 text-5xl font-semibold leading-tight">
                  MiniCoreBank
                  <span className="block bg-gradient-to-r from-indigo-200 to-cyan-200 bg-clip-text text-transparent">
                    Create account
                  </span>
                </h1>

                <p className="mt-4 max-w-md text-sm text-slate-200/80">
                  Tạo tài khoản để bắt đầu sử dụng hệ thống.
                </p>
              </div>

              <p className="text-xs text-slate-200/60">
                Tip: dùng username dễ nhớ & mật khẩu mạnh. • {year}
              </p>
            </div>
          </div>

          {/* Right */}
          <div className="flex h-screen items-center justify-center p-6 sm:p-12">
            <div className="w-full max-w-xl rounded-3xl border border-white/10 bg-white/5 p-6 shadow-2xl shadow-black/40 backdrop-blur-xl sm:p-10">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <h2 className="text-3xl font-semibold">Đăng ký</h2>
                  <p className="mt-2 text-sm text-slate-200/70">
                    Nhập thông tin để tạo tài khoản.
                  </p>
                </div>

                <button
                  type="button"
                  className="rounded-xl border border-white/10 bg-white/5 px-3 py-2 text-xs text-slate-200/80 hover:bg-white/10"
                  onClick={() => navigate("/login")}
                >
                  Về đăng nhập
                </button>
              </div>

              {error && (
                <div className="mt-5 rounded-2xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-100">
                  {error}
                </div>
              )}

              <form className="mt-6 space-y-4" onSubmit={onSubmit}>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <Field label="Họ và tên">
                    <Input value={fullName} onChange={setFullName} placeholder="Nguyễn Văn A" />
                  </Field>

                  <Field label="Username">
                    <Input value={username} onChange={setUsername} placeholder="nd2011" />
                    {usernameInvalid && <Hint>tối thiểu 4 ký tự</Hint>}
                  </Field>
                </div>

                <Field label="Email">
                  <Input value={email} onChange={setEmail} placeholder="you@company.com" />
                  {emailInvalid && <Hint>Email không hợp lệ</Hint>}
                </Field>

                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <Field label="Số điện thoại">
                    <Input value={phone} onChange={setPhone} placeholder="+84901234567" />
                    {phoneInvalid && <Hint>8–15 số, có thể có +</Hint>}
                  </Field>

                  <Field label="Currency">
                    <select
                      value={currency}
                      onChange={(e) => setCurrency(e.target.value)}
                      className="w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-slate-900 outline-none focus:border-indigo-400/60 focus:bg-white/10"
                    >
                      <option value="VND">VND</option>
                      <option value="USD">USD</option>
                      <option value="EUR">EUR</option>
                      <option value="THB">THB</option>
                    </select>
                  </Field>
                </div>

                <Field label="Mật khẩu">
                  <div className="rounded-2xl border border-white/10 bg-white/5 focus-within:border-indigo-400/60 focus-within:bg-white/10">
                    <div className="flex items-center">
                      <input
                        type={showPassword ? "text" : "password"}
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="••••••••"
                        className="w-full bg-transparent px-4 py-3 text-slate-100 placeholder:text-slate-400 outline-none"
                      />
                      <button
                        type="button"
                        className="mr-2 rounded-xl border border-white/10 bg-white/5 px-3 py-2 text-xs text-slate-200/80 hover:bg-white/10"
                        onClick={() => setShowPassword((v) => !v)}
                      >
                        {showPassword ? "Hide" : "Show"}
                      </button>
                    </div>
                  </div>
                  {passwordInvalid && <Hint>tối thiểu 6 ký tự</Hint>}
                </Field>

                <button
                  disabled={loading}
                  type="submit"
                  className="mt-2 w-full rounded-2xl bg-gradient-to-r from-indigo-500 to-cyan-500 px-4 py-3 font-semibold text-white shadow-lg shadow-indigo-500/20 transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-60 active:scale-[0.99]"
                >
                  {loading ? "Đang tạo tài khoản..." : "Tạo tài khoản"}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="space-y-2">
      <label className="text-sm text-slate-200/80">{label}</label>
      {children}
    </div>
  )
}

function Input({
  value,
  onChange,
  placeholder,
}: {
  value: string
  onChange: (v: string) => void
  placeholder?: string
}) {
  return (
    <div className="rounded-2xl border border-white/10 bg-white/5 focus-within:border-indigo-400/60 focus-within:bg-white/10">
      <input
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="w-full bg-transparent px-4 py-3 text-slate-100 placeholder:text-slate-400 outline-none"
      />
    </div>
  )
}

function Hint({ children }: { children: React.ReactNode }) {
  return <p className="text-xs text-rose-200/90">{children}</p>
}