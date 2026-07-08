import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Building2, CheckCircle, Handshake, Mail, PackageCheck, Rocket, Wrench } from 'lucide-react';
import SiteFooter from '../components/SiteFooter';

const partnershipTypes = [
  {
    title: 'Technology suppliers',
    description: 'Hardware, sensors, energy systems, batteries, software, and automation platforms that strengthen our product ecosystem.',
    icon: PackageCheck,
  },
  {
    title: 'Deployment partners',
    description: 'Installers, integrators, service providers, and field teams that help deliver reliable customer outcomes.',
    icon: Wrench,
  },
  {
    title: 'Enterprise channels',
    description: 'Distributors, resellers, project developers, and institutions looking to bring robotics, drones, or solar to new markets.',
    icon: Building2,
  },
];

const partnerStandards = [
  'Clear technical capability or market access',
  'A commitment to safe, ethical, and compliant deployments',
  'Responsive communication and accountable delivery',
  'A practical plan for customer support after deployment',
];

const processSteps = [
  ['01', 'Introduce', 'Share your organization, capabilities, region, and proposed partnership model.'],
  ['02', 'Evaluate', 'We review fit across technical scope, commercial value, delivery readiness, and customer impact.'],
  ['03', 'Pilot', 'Aligned partners start with a focused pilot, channel test, or project-specific collaboration.'],
  ['04', 'Scale', 'Successful pilots move into structured agreements, shared planning, and ongoing operations.'],
];

export default function Partnerships() {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-950">
      <section className="bg-slate-950 pt-28 text-white">
        <div className="mx-auto grid max-w-7xl gap-12 px-6 pb-16 pt-10 lg:grid-cols-[1fr_0.9fr] lg:items-center">
          <div>
            <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-violet-300/30 bg-violet-300/10 px-4 py-2 text-sm font-semibold text-violet-100">
              <Handshake className="h-4 w-4" />
              Strategic partnerships
            </div>
            <h1 className="max-w-4xl text-5xl font-bold leading-tight tracking-normal md:text-6xl">
              Partner with Allytic Labs
            </h1>
            <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-300">
              We work with serious suppliers, deployment teams, institutions, and channel partners who can help bring robotics, drones, and solar technology into real-world use.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <a href="mailto:business@ailyticslabs.com" className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition hover:bg-blue-700">
                Start partnership inquiry
                <Mail className="h-5 w-5" />
              </a>
              <Link to="/contact" className="inline-flex items-center justify-center rounded-lg border border-white/30 px-6 py-3 font-semibold text-white transition hover:bg-white/10">
                Contact team
              </Link>
            </div>
          </div>

          <div className="rounded-lg border border-white/10 bg-white/5 p-6">
            <Rocket className="h-10 w-10 text-cyan-300" />
            <h2 className="mt-5 text-2xl font-bold tracking-normal">Built for mutual growth</h2>
            <p className="mt-4 text-sm leading-6 text-slate-300">
              The best partnerships combine credible technology, field readiness, market understanding, and a shared responsibility to customers.
            </p>
          </div>
        </div>
      </section>

      <main>
        <section className="mx-auto max-w-7xl px-6 py-16">
          <div className="max-w-3xl">
            <p className="text-sm font-bold uppercase text-blue-700">Partnership Tracks</p>
            <h2 className="mt-3 text-3xl font-bold tracking-normal text-slate-950 md:text-4xl">
              Where collaboration can create value.
            </h2>
          </div>

          <div className="mt-10 grid gap-6 md:grid-cols-3">
            {partnershipTypes.map(({ title, description, icon: Icon }) => (
              <article key={title} className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-50 text-blue-700">
                  <Icon className="h-6 w-6" />
                </div>
                <h3 className="mt-5 text-xl font-bold text-slate-950">{title}</h3>
                <p className="mt-3 text-sm leading-6 text-slate-600">{description}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="border-y border-slate-200 bg-white">
          <div className="mx-auto grid max-w-7xl gap-10 px-6 py-16 lg:grid-cols-[0.85fr_1.15fr]">
            <div>
              <p className="text-sm font-bold uppercase text-blue-700">Partner Standard</p>
              <h2 className="mt-3 text-3xl font-bold tracking-normal text-slate-950">We look for partners who can execute.</h2>
              <p className="mt-5 text-base leading-7 text-slate-700">
                Allytic Labs is interested in partnerships that improve customer outcomes, expand responsible access to technology, and reduce deployment risk.
              </p>
            </div>
            <div className="grid gap-4">
              {partnerStandards.map((standard) => (
                <div key={standard} className="flex gap-4 rounded-lg border border-slate-200 bg-slate-50 p-4">
                  <CheckCircle className="mt-0.5 h-5 w-5 shrink-0 text-emerald-600" />
                  <p className="text-sm font-semibold leading-6 text-slate-800">{standard}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="mx-auto max-w-7xl px-6 py-16">
          <div className="mb-10 flex flex-col justify-between gap-4 md:flex-row md:items-end">
            <div>
              <p className="text-sm font-bold uppercase text-blue-700">Process</p>
              <h2 className="mt-3 text-3xl font-bold tracking-normal text-slate-950">How partnership conversations move forward.</h2>
            </div>
            <a href="mailto:business@ailyticslabs.com" className="inline-flex items-center gap-2 font-semibold text-blue-700 hover:text-blue-800">
              Email business team
              <ArrowRight className="h-5 w-5" />
            </a>
          </div>

          <div className="grid gap-5 md:grid-cols-4">
            {processSteps.map(([number, title, description]) => (
              <article key={number} className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
                <p className="text-sm font-bold text-blue-700">{number}</p>
                <h3 className="mt-4 text-lg font-bold text-slate-950">{title}</h3>
                <p className="mt-3 text-sm leading-6 text-slate-600">{description}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="mx-auto max-w-7xl px-6 pb-16">
          <div className="rounded-lg bg-slate-950 p-8 text-white md:p-10">
            <h2 className="text-3xl font-bold tracking-normal">Ready to propose a partnership?</h2>
            <p className="mt-4 max-w-3xl text-base leading-7 text-slate-300">
              Send a concise overview of your organization, region, capabilities, and the partnership model you have in mind.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <a href="mailto:business@ailyticslabs.com" className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition hover:bg-blue-700">
                business@ailyticslabs.com
                <Mail className="h-5 w-5" />
              </a>
              <Link to="/contact" className="inline-flex items-center justify-center rounded-lg border border-white/30 px-6 py-3 font-semibold text-white transition hover:bg-white/10">
                Use contact form
              </Link>
            </div>
          </div>
        </section>
      </main>
      <SiteFooter />
    </div>
  );
}
