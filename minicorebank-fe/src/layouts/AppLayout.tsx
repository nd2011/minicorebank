import { NavLink, Outlet, useNavigate } from "react-router-dom"
import { useMemo, useState } from "react"

function cx(...c: Array<string | false | undefined>) {
  return c.filter(Boolean).join(" ")
}

export default function AppLayout() {
  const navigate = useNavigate()
  const [collapsed, setCollapsed] = useState(false)

const role = localStorage.getItem("role") ?? "GUEST"
const customerId = localStorage.getItem("customerId") ?? ""

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <div className="flex min-h-screen">
        {/* Sidebar */}
        <aside
          className={cx(
            "border-r border-white/10 bg-white/5 backdrop-blur-xl",
            collapsed ? "w-16" : "w-64"
          )}
        >
          <div className="relative z-10 flex h-16 items-center justify-between px-4">
            <div className="flex items-center gap-2">
              <div className="h-9 w-9 rounded-xl bg-gradient-to-r from-indigo-500 to-cyan-500" />
              {!collapsed && (
                <div className="leading-tight">
                  <div className="font-semibold">MiniCoreBank</div>
                  <div className="text-xs text-white/60">Dashboard</div>
                </div>
              )}
            </div>

            <button
            type="button"
            aria-label={collapsed ? "Mở menu" : "Thu gọn menu"}
            onClick={() => setCollapsed((v) => !v)}
            className="ml-2 inline-flex h-10 w-10 items-center justify-center rounded-xl border border-white/10 bg-white/5 hover:bg-white/10 active:scale-95 cursor-pointer z-20"
            >
            {collapsed ? "»" : "«"}
            </button>
          </div>

          <nav className="px-2 pb-4">
            <NavItem to="/dashboard" collapsed={collapsed} label="Dashboard" />
            <NavItem to="/customers" collapsed={collapsed} label="Customers" />
            <NavItem to="/accounts" collapsed={collapsed} label="Accounts" />
            <NavItem to="/tx" collapsed={collapsed} label="Transactions" />
          </nav>

          <div className="mt-auto border-t border-white/10 p-3">
            {!collapsed && (
              <div className="mb-3 rounded-2xl border border-white/10 bg-white/5 p-3 text-xs text-white/70">
                Role: <span className="text-white">{role}</span>
                <br />
                CustomerId: <span className="text-white">{customerId || "-"}</span>
              </div>
            )}

            <button
              className={cx(
                "w-full rounded-2xl border border-white/10 bg-white/5 px-3 py-2 text-sm hover:bg-white/10",
                collapsed && "px-0"
              )}
              onClick={() => {
                localStorage.removeItem("token")
                localStorage.removeItem("tokenType")
                localStorage.removeItem("role")
                localStorage.removeItem("customerId")
                navigate("/login")
              }}
            >
              {collapsed ? "⎋" : "Logout"}
            </button>
          </div>
        </aside>

        {/* Main */}
        <div className="flex min-h-screen flex-1 flex-col">
          {/* Header */}
          <header className="sticky top-0 z-10 h-16 border-b border-white/10 bg-slate-950/60 backdrop-blur">
            <div className="flex h-16 items-center justify-between px-6">
              <div>
                <div className="text-sm text-white/70">Xin chào 👋</div>
                <div className="font-semibold">MiniCoreBank Portal</div>
              </div>

              <div className="flex items-center gap-2">
                <button className="rounded-xl border border-white/10 bg-white/5 px-3 py-2 text-sm hover:bg-white/10">
                  Notifications
                </button>
                <div className="h-10 w-10 rounded-full border border-white/10 bg-white/5" />
              </div>
            </div>
          </header>

          {/* Content */}
          <main className="flex-1 p-6">
            <Outlet />
          </main>

          {/* Footer */}
          <footer className="border-t border-white/10 bg-white/5 px-6 py-4 text-sm text-white/60">
            © {new Date().getFullYear()} MiniCoreBank • Built with React + Vite
          </footer>
        </div>
      </div>
    </div>
  )
}

function NavItem({
  to,
  label,
  collapsed,
}: {
  to: string
  label: string
  collapsed: boolean
}) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        cx(
          "mb-1 flex items-center gap-3 rounded-2xl px-3 py-2 text-sm transition",
          "hover:bg-white/10",
          isActive ? "bg-white/10 border border-white/10" : "text-white/80",
          collapsed && "justify-center px-0"
        )
      }
      title={collapsed ? label : undefined}
    >
      <span className="inline-flex h-8 w-8 items-center justify-center rounded-xl border border-white/10 bg-white/5">
        •
      </span>
      {!collapsed && <span>{label}</span>}
    </NavLink>
  )
}