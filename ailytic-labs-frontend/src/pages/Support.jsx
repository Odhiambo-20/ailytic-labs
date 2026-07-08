import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Headphones, Mail, Wrench } from 'lucide-react';
import SiteFooter from '../components/SiteFooter';

const supportAreas = [
  ['Product guidance', 'Choosing robotics, drone, or solar systems for your operating environment.'],
  ['Deployment support', 'Site readiness, training, troubleshooting, and handover planning.'],
  ['Service requests', 'Maintenance coordination, warranty questions, and technical follow-up.'],
];

function Support() {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-950">
      <section className="bg-slate-950 pt-28 text-white">
        <div className="mx-auto max-w-7xl px-6 pb-16 pt-10">
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-cyan-300/30 bg-cyan-300/10 px-4 py-2 text-sm font-semibold text-cyan-100">
            <Headphones className="h-4 w-4" />
            Support
          </div>
          <h1 className="max-w-4xl text-5xl font-bold leading-tight tracking-normal md:text-6xl">Get help from the Bella team.</h1>
          <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-300">
            Route your request to the right team for product, deployment, service, or partnership support.
          </p>
        </div>
      </section>

      <main>
        <section className="mx-auto max-w-7xl px-6 py-16">
          <div className="grid gap-6 md:grid-cols-3">
            {supportAreas.map(([title, description]) => (
              <article key={title} className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
                <Wrench className="h-8 w-8 text-blue-700" />
                <h2 className="mt-5 text-xl font-bold">{title}</h2>
                <p className="mt-3 text-sm leading-6 text-slate-600">{description}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="mx-auto max-w-7xl px-6 pb-16">
          <div className="rounded-lg bg-slate-950 p-8 text-white md:p-10">
            <h2 className="text-3xl font-bold tracking-normal">Need direct assistance?</h2>
            <p className="mt-4 max-w-3xl text-base leading-7 text-slate-300">
              Include your product area, order or project context, location, and the issue you need solved.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <Link to="/contact" className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition hover:bg-blue-700">
                Contact support
                <ArrowRight className="h-5 w-5" />
              </Link>
              <a href="mailto:support@ailyticslabs.com" className="inline-flex items-center justify-center gap-2 rounded-lg border border-white/30 px-6 py-3 font-semibold text-white transition hover:bg-white/10">
                <Mail className="h-5 w-5" />
                support@ailyticslabs.com
              </a>
            </div>
          </div>
        </section>
      </main>

      <SiteFooter />
    </div>
  );
}

export default Support;
