import { Link } from 'react-router-dom';

/**
 * Official TechVault logo – used in Navbar, Footer, and auth pages.
 * Designed to instantly signal a premium tech e-commerce store.
 */
export default function Logo({ size = 'md', showWordmark = true, className = '', to = '/' }) {
    const sizes = {
        sm: { box: 'h-8 w-8', icon: 'h-4 w-4', text: 'text-base' },
        md: { box: 'h-9 w-9', icon: 'h-5 w-5', text: 'text-lg' },
        lg: { box: 'h-11 w-11', icon: 'h-6 w-6', text: 'text-xl' },
    };
    const s = sizes[size] || sizes.md;

    const content = (
        <div className={`flex items-center gap-2.5 ${className}`}>
            {/* Icon mark – circuit + vault shield feel */}
            <div
                className={`relative flex ${s.box} shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-brand-600 via-brand-700 to-brand-900 shadow-glow ring-1 ring-white/10`}
            >
                <svg className={`${s.icon} text-white`} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75">
                    {/* Stylised chip / circuit board */}
                    <rect x="4" y="4" width="16" height="16" rx="2.5" />
                    <path d="M9 4v3M15 4v3M9 17v3M15 17v3M4 9h3M4 15h3M17 9h3M17 15h3" strokeLinecap="round" />
                    <circle cx="12" cy="12" r="2.5" fill="currentColor" stroke="none" />
                </svg>
                {/* Accent dot */}
                <span className="absolute -right-0.5 -top-0.5 h-2.5 w-2.5 rounded-full bg-accent-400 ring-2 ring-white" />
            </div>

            {showWordmark && (
                <div className="flex flex-col leading-none">
                    <span className={`font-display font-extrabold tracking-tight text-slate-900 ${s.text}`}>
                        Tech<span className="text-brand-600">Vault</span>
                    </span>
                    <span className="mt-0.5 hidden text-[10px] font-medium uppercase tracking-[0.18em] text-slate-400 sm:block">
                        Premium Tech Store
                    </span>
                </div>
            )}
        </div>
    );

    if (to) {
        return (
            <Link to={to} className="group shrink-0 transition hover:opacity-90" aria-label="TechVault Home">
                {content}
            </Link>
        );
    }
    return content;
}
