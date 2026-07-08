import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, BriefcaseBusiness, Cpu, Leaf, ShieldCheck } from 'lucide-react';
import SiteFooter from '../components/SiteFooter';

const roles = [
  ['Robotics field engineer', 'Deployment, diagnostics, and maintenance for automation systems.'],
  ['Drone operations specialist', 'Flight planning, payload workflows, customer training, and field safety.'],
  ['Solar project coordinator', 'Site readiness, installer coordination, and customer handover.'],
];

function Careers() {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-950">
      <section className="bg-slate-950 pt-28 text-white">
        <div className="mx-auto max-w-7xl px-6 pb-16 pt-10">
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-cyan-300/30 bg-cyan-300/10 px-4 py-2 text-sm font-semibold text-cyan-100">
            <BriefcaseBusiness className="h-4 w-4" />
            Careers
          </div>
          <h1 className="max-w-4xl text-5xl font-bold leading-tight tracking-normal md:text-6xl">Build practical technology with Bella.</h1>
          <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-300">
            We look for people who can move between engineering judgment, field realities, customer care, and careful execution.
          </p>
        </div>
      </section>

      <main>
        <section className="mx-auto max-w-7xl px-6 py-16">
          <div className="grid gap-6 md:grid-cols-3">
            {[Cpu, ShieldCheck, Leaf].map((Icon, index) => (
              <article key={roles[index][0]} className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
                <Icon className="h-8 w-8 text-blue-700" />
                <h2 className="mt-5 text-xl font-bold">{roles[index][0]}</h2>
                <p className="mt-3 text-sm leading-6 text-slate-600">{roles[index][1]}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="mx-auto max-w-7xl px-6 pb-16">
          <div className="rounded-lg bg-slate-950 p-8 text-white md:p-10">
            <h2 className="text-3xl font-bold tracking-normal">Interested in joining?</h2>
            <p className="mt-4 max-w-3xl text-base leading-7 text-slate-300">
              Send your CV, area of interest, and examples of relevant work. We review applications based on practical capability and reliability.
            </p>
            <a href="mailto:recruitment@ailyticslabs.com" className="mt-8 inline-flex items-center gap-2 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition hover:bg-blue-700">
              recruitment@ailyticslabs.com
              <ArrowRight className="h-5 w-5" />
            </a>
          </div>
        </section>
      </main>

      <SiteFooter />
    </div>
  );
}

export default Careers;
