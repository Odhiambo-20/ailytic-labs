import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Newspaper } from 'lucide-react';
import SiteFooter from '../components/SiteFooter';

const updates = [
  ['Robotics deployments', 'Field-ready automation remains our focus across agriculture, inspection, and service workflows.'],
  ['Drone systems', 'We continue to evaluate payload, mapping, and monitoring use cases for practical operations.'],
  ['Solar technology', 'Energy resilience is central to our customer conversations across homes, businesses, and institutions.'],
];

function News() {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-950">
      <section className="bg-slate-950 pt-28 text-white">
        <div className="mx-auto max-w-7xl px-6 pb-16 pt-10">
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-violet-300/30 bg-violet-300/10 px-4 py-2 text-sm font-semibold text-violet-100">
            <Newspaper className="h-4 w-4" />
            News
          </div>
          <h1 className="max-w-4xl text-5xl font-bold leading-tight tracking-normal md:text-6xl">Company updates and technology notes.</h1>
          <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-300">
            Follow what Bella is building across robotics, drones, solar, and deployment partnerships.
          </p>
        </div>
      </section>

      <main className="mx-auto max-w-7xl px-6 py-16">
        <div className="grid gap-6 md:grid-cols-3">
          {updates.map(([title, description]) => (
            <article key={title} className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
              <p className="text-sm font-bold uppercase text-blue-700">Update</p>
              <h2 className="mt-4 text-xl font-bold">{title}</h2>
              <p className="mt-3 text-sm leading-6 text-slate-600">{description}</p>
              <Link to="/contact" className="mt-5 inline-flex items-center gap-2 text-sm font-semibold text-blue-700 hover:text-blue-800">
                Ask about this
                <ArrowRight className="h-4 w-4" />
              </Link>
            </article>
          ))}
        </div>
      </main>

      <SiteFooter />
    </div>
  );
}

export default News;
