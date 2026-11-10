// Glowing line-art computers (monitor, laptop, tower, phone) + circuit traces.
// Pure SVG, so it works offline and stays sharp on any screen.
const codeColors = ['#22d3ee', '#748ffc', '#a78bfa', '#34d399', '#f472b6'];

function CodeLines({ x, y, rows, width, gap = 14 }) {
    const widths = [0.9, 0.55, 0.75, 0.4, 0.85, 0.6, 0.7, 0.5, 0.8, 0.45];
    return rows.map((_, i) => (
        <rect
            key={i}
            x={x + (i % 3) * 14}
            y={y + i * gap}
            width={width * widths[i % widths.length] - (i % 3) * 14}
            height="5"
            rx="2.5"
            fill={codeColors[i % codeColors.length]}
            className="animate-code"
            style={{ animationDelay: `${i * 0.25}s` }}
        />
    ));
}

export default function TechBackdrop({ strong = true }) {
    return (
        <div
            aria-hidden="true"
            className={`pointer-events-none absolute inset-0 overflow-hidden ${strong ? 'opacity-90' : 'opacity-40'}`}
        >
            <svg
                className="h-full w-full"
                viewBox="0 0 1200 700"
                preserveAspectRatio="xMidYMid slice"
                fill="none"
            >
                <defs>
                    <linearGradient id="tb-stroke" x1="0" y1="0" x2="1" y2="1">
                        <stop offset="0" stopColor="#22d3ee" />
                        <stop offset="1" stopColor="#5c7cfa" />
                    </linearGradient>
                    <linearGradient id="tb-screen" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0" stopColor="#0f1b4d" stopOpacity="0.95" />
                        <stop offset="1" stopColor="#0a1030" stopOpacity="0.95" />
                    </linearGradient>
                    <filter id="tb-glow" x="-20%" y="-20%" width="140%" height="140%">
                        <feGaussianBlur stdDeviation="3" result="b" />
                        <feMerge>
                            <feMergeNode in="b" />
                            <feMergeNode in="SourceGraphic" />
                        </feMerge>
                    </filter>
                </defs>

                {/* Circuit traces */}
                <g stroke="url(#tb-stroke)" strokeWidth="1.5" opacity="0.5" strokeDasharray="6 14" className="animate-dash">
                    <path d="M0 120 H180 L220 160 H420" />
                    <path d="M0 560 H140 L180 520 H360 L400 560 H520" />
                    <path d="M1200 90 H1010 L970 130 H860" />
                    <path d="M1200 610 H1040 L1000 570 H880" />
                    <path d="M600 0 V70 L630 100 V180" />
                    <path d="M520 700 V640 L550 610 V560" />
                </g>
                <g fill="#22d3ee" opacity="0.7">
                    <circle cx="420" cy="160" r="4" />
                    <circle cx="520" cy="560" r="4" />
                    <circle cx="860" cy="130" r="4" />
                    <circle cx="880" cy="570" r="4" />
                    <circle cx="630" cy="180" r="4" />
                </g>

                {/* Big monitor */}
                <g transform="translate(180 -40) scale(0.8)">
                    <g className="animate-float-slow" filter="url(#tb-glow)">
                        <rect x="700" y="150" width="380" height="240" rx="14" fill="url(#tb-screen)" stroke="url(#tb-stroke)" strokeWidth="3" />
                        <rect x="716" y="166" width="348" height="208" rx="6" stroke="#5c7cfa" strokeOpacity="0.4" />
                        <CodeLines x={736} y={190} rows={Array(10).fill(0)} width={300} gap={17} />
                        <path d="M890 390 V430 M820 440 H960" stroke="url(#tb-stroke)" strokeWidth="3" strokeLinecap="round" />
                    </g>
                </g>

                {/* Laptop */}
                <g transform="translate(728 356) scale(0.55)">
                    <g className="animate-float" filter="url(#tb-glow)" style={{ animationDelay: '1s' }}>
                        <path d="M180 430 L250 330 H470 L540 430 Z" fill="url(#tb-screen)" stroke="url(#tb-stroke)" strokeWidth="3" strokeLinejoin="round" />
                        <CodeLines x={270} y={350} rows={Array(5).fill(0)} width={190} gap={15} />
                        <path d="M150 430 H570 L590 462 H130 Z" fill="#0a1030" stroke="url(#tb-stroke)" strokeWidth="3" strokeLinejoin="round" />
                        <path d="M330 446 H390" stroke="#22d3ee" strokeWidth="3" strokeLinecap="round" />
                    </g>
                </g>

                {/* Desktop tower */}
                <g transform="translate(0 -200)">
                    <g className="animate-float" filter="url(#tb-glow)" style={{ animationDelay: '2s' }}>
                        <rect x="1070" y="330" width="86" height="190" rx="10" fill="#0a1030" stroke="url(#tb-stroke)" strokeWidth="3" />
                        <path d="M1088 360 H1138 M1088 376 H1138 M1088 392 H1138" stroke="#5c7cfa" strokeWidth="2" strokeLinecap="round" />
                        <circle cx="1113" cy="470" r="14" stroke="#22d3ee" strokeWidth="2.5" />
                        <circle cx="1113" cy="470" r="4" fill="#22d3ee" className="animate-code" />
                    </g>
                </g>

                {/* Phone */}
                <g transform="translate(0 -60)">
                    <g className="animate-float-slow" filter="url(#tb-glow)" style={{ animationDelay: '3s' }}>
                        <rect x="610" y="440" width="84" height="150" rx="14" fill="url(#tb-screen)" stroke="url(#tb-stroke)" strokeWidth="3" />
                        <CodeLines x={624} y={470} rows={Array(5).fill(0)} width={56} gap={14} />
                        <path d="M638 578 H666" stroke="#22d3ee" strokeWidth="3" strokeLinecap="round" />
                    </g>
                </g>

                {/* Chip */}
                <g transform="translate(480 -100)">
                    <g className="animate-float" stroke="url(#tb-stroke)" strokeWidth="2" opacity="0.8" style={{ animationDelay: '1.5s' }}>
                        <rect x="90" y="190" width="70" height="70" rx="8" />
                        <rect x="108" y="208" width="34" height="34" rx="4" />
                        <path d="M110 190 V176 M125 190 V176 M140 190 V176 M110 260 V274 M125 260 V274 M140 260 V274 M90 210 H76 M90 225 H76 M90 240 H76 M160 210 H174 M160 225 H174 M160 240 H174" strokeLinecap="round" />
                    </g>
                </g>
            </svg>
        </div>
    );
}
