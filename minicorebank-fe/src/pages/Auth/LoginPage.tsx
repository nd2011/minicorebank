import { login } from "../../services/auth"
import { useMemo, useState } from "react"
import { useNavigate } from "react-router-dom"

type LoginPageProps = {
  onGoRegister?: () => void
}
export default function LoginPage({ onGoRegister }: LoginPageProps) {
  const navigate = useNavigate()
  const year = useMemo(() => new Date().getFullYear(), [])
  const [showPassword, setShowPassword] = useState(false)
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  return (
    <div className="relative min-h-screen overflow-hidden bg-slate-950 text-slate-100">
      {/* Background */}
      <div className="pointer-events-none absolute inset-0">
        <div className="absolute -left-44 -top-44 h-[540px] w-[540px] rounded-full bg-indigo-600/30 blur-3xl" />
        <div className="absolute -right-44 top-10 h-[540px] w-[540px] rounded-full bg-cyan-500/20 blur-3xl" />
        <div className="absolute bottom-[-240px] left-1/2 h-[540px] w-[540px] -translate-x-1/2 rounded-full bg-fuchsia-500/10 blur-3xl" />
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_1px_1px,rgba(255,255,255,0.06)_1px,transparent_0)] [background-size:18px_18px]" />
      </div>

      <div className="relative min-h-screen w-full">
        <div className="grid min-h-screen w-full grid-cols-1 md:grid-cols-2">
          {/* Left */}
          <div className="relative hidden md:block">
            <div className="absolute inset-0 bg-gradient-to-br from-indigo-500/25 via-white/0 to-cyan-400/10" />
            <div className="relative flex min-h-screen flex-col justify-between p-12">
              <div>
                <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/5 px-4 py-2 text-sm">
                  <span className="h-2 w-2 rounded-full bg-emerald-400" />
                  Core Banking • Admin Portal
                </div>

                <h1 className="mt-7 text-5xl font-semibold leading-tight">
                  MiniCoreBank
                  <span className="block bg-gradient-to-r from-indigo-200 to-cyan-200 bg-clip-text text-transparent">
                    Secure sign-in
                  </span>
                </h1>

                <div className="mt-7 grid grid-cols-2 gap-3 text-xs text-slate-200/75">
                  {[
                    ["MFA-ready", "Bảo mật nhiều lớp"],
                    ["Audit logs", "Truy vết thao tác"],
                    ["RBAC", "Phân quyền rõ ràng"],
                    ["SSO", "Đăng nhập doanh nghiệp"],
                  ].map(([t, d]) => (
                    <div key={t} className="rounded-2xl border border-white/10 bg-white/5 p-4">
                      <p className="font-medium text-slate-100">{t}</p>
                      <p className="mt-1 text-slate-200/70">{d}</p>
                    </div>
                  ))}
                </div>
              </div>

              <p className="text-xs text-slate-200/60">
                Tip: dùng email công ty & mật khẩu mạnh. • {year}
              </p>
            </div>
          </div>

          {/* Right */}
          <div className="flex min-h-screen items-center justify-center p-6 sm:p-12">
            <div className="w-full max-w-xl rounded-3xl border border-white/10 bg-white/5 p-6 shadow-2xl shadow-black/40 backdrop-blur-xl sm:p-10">
              <div className="md:hidden">
                <div className="inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-slate-200/80">
                  <span className="h-2 w-2 rounded-full bg-emerald-400" />
                  MiniCoreBank • Admin
                </div>
              </div>

              <h2 className="mt-5 text-3xl font-semibold">Đăng nhập</h2>
              <p className="mt-2 text-sm text-slate-200/70">
                Chào mừng bạn quay lại. Hãy nhập thông tin để tiếp tục.
              </p>
                  {error && (
                      <div className="mt-5 rounded-2xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-100">
                        {error}
                      </div>
                    )}
              <form
                className="mt-8 space-y-5"
                onSubmit={async (e) => {
                  e.preventDefault()
                  setError(null)

                  try {
                    setLoading(true)
                    const res = await login({ username, password })
                    localStorage.setItem("token", res.accessToken)
                    localStorage.setItem("tokenType", res.tokenType)
                    localStorage.setItem("role", res.role) // (tuỳ chọn)
                    localStorage.setItem("customerId", String(res.customerId ?? ""))
                    navigate("/dashboard")
                  } catch (err: any) {
                    setError(err?.response?.data?.message ?? "Đăng nhập thất bại")
                  } finally {
                    setLoading(false)
                  }
                }}
              >
                <div className="space-y-2">
                  <label className="text-sm text-slate-200/80">Tên người dùng</label>
                  <div className="rounded-2xl border border-white/10 bg-white/5 focus-within:border-indigo-400/60 focus-within:bg-white/10">
                    <input
                      value={username}
                      onChange={(e) => setUsername(e.target.value)}
                      required
                      placeholder="username"
                      className="w-full bg-transparent px-4 py-3 text-slate-100 placeholder:text-slate-400 outline-none"
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <label className="text-sm text-slate-200/80">Mật khẩu</label>
                    <button
                      type="button"
                      className="text-sm text-indigo-200/90 hover:text-indigo-200"
                      onClick={() => alert("Forgot password (demo)")}
                    >
                      Quên mật khẩu?
                    </button>
                  </div>

                  <div className="rounded-2xl border border-white/10 bg-white/5 focus-within:border-indigo-400/60 focus-within:bg-white/10">
                    <div className="flex items-center">
                      <input
                        type={showPassword ? "text" : "password"}
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
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
                </div>

                <div className="flex items-center justify-between">
                  <label className="flex cursor-pointer items-center gap-2 text-sm text-slate-200/80">
                    <input
                      type="checkbox"
                      className="h-4 w-4 rounded border-white/20 bg-white/10 text-indigo-500"
                      defaultChecked
                    />
                    Ghi nhớ đăng nhập
                  </label>

                  <span className="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-slate-200/70">
                    v1.0
                  </span>
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full rounded-2xl bg-gradient-to-r from-indigo-500 to-cyan-500 px-4 py-3 font-semibold text-white shadow-lg shadow-indigo-500/20 transition hover:brightness-110 active:scale-[0.99]"
                >
                  {loading ? "Đang đăng nhập..." : "Đăng nhập"}
                </button>

                <div className="relative py-2">
                  <div className="absolute inset-0 flex items-center">
                    <div className="w-full border-t border-white/10" />
                  </div>
                  <div className="relative flex justify-center">
                    <span className="bg-slate-950/0 px-3 text-xs text-slate-200/60">hoặc</span>
                  </div>
                </div>

                <button
                  type="button"
                  className="w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 font-semibold text-slate-100 transition hover:bg-white/10 active:scale-[0.99]"
                  onClick={() => alert("SSO (demo)")}
                >
                  Đăng nhập bằng SSO
                </button>

                <button
                type="button"
                className="w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 font-semibold text-slate-100 transition hover:bg-white/10 active:scale-[0.99]"
                onClick={() => navigate("/register") /* or onGoRegister?.() */}
                >
                Chưa có tài khoản? Đăng ký
                </button>

                <p className="pt-2 text-center text-xs text-slate-200/60">
                  Bằng việc đăng nhập, bạn đồng ý với{" "}
                  <span className="text-indigo-200/90">Terms</span> &{" "}
                  <span className="text-indigo-200/90">Privacy</span>.
                </p>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

