import React, { useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  AlertCircle,
  ArrowRight,
  Building2,
  CheckCircle,
  Clock3,
  Github,
  Headphones,
  Instagram,
  Linkedin,
  Mail,
  MapPin,
  MessageSquare,
  Phone,
  Send,
  ShieldCheck,
  Youtube,
} from 'lucide-react';
import BrandLogo from '../components/BrandLogo';
import { contactAPI } from '../services/api';

const inquiryTypes = [
  'Product Information',
  'Sales Inquiry',
  'Technical Support',
  'Partnership Inquiry',
  'Project Consultation',
  'Media Inquiry',
  'Other',
];

const contactChannels = [
  {
    label: 'Sales & Projects',
    detail: 'Talk to us about robotics, drones, solar systems, and deployment scope.',
    value: 'business@ailyticslabs.com',
    href: 'mailto:business@ailyticslabs.com',
    icon: Building2,
  },
  {
    label: 'General Support',
    detail: 'For product questions, active orders, service requests, and follow-up.',
    value: 'info@ailyticslabs.com',
    href: 'mailto:info@ailyticslabs.com',
    icon: Headphones,
  },
  {
    label: 'Phone',
    detail: 'Available during business hours for urgent coordination.',
    value: '+254 748 630 243',
    href: 'tel:+254748630243',
    icon: Phone,
  },
];

const responseCommitments = [
  'Qualified inquiries routed to the right team',
  'Business responses within one working day',
  'No job applications processed through this form',
];

const Contact = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    company: '',
    helpType: 'Product Information',
    message: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitStatus, setSubmitStatus] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');

  const messageCharacters = formData.message.trim().length;
  const canSubmit = useMemo(
    () => formData.firstName.trim() && formData.email.trim() && formData.message.trim(),
    [formData.email, formData.firstName, formData.message]
  );

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData((previous) => ({ ...previous, [name]: value }));

    if (submitStatus) {
      setSubmitStatus(null);
      setErrorMessage('');
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!canSubmit) {
      setSubmitStatus('error');
      setErrorMessage('Please complete your name, email, and message before sending.');
      return;
    }

    setIsSubmitting(true);
    setSubmitStatus(null);
    setErrorMessage('');

    const enrichedMessage = [
      formData.message.trim(),
      formData.company.trim() ? `Company: ${formData.company.trim()}` : null,
      formData.phone.trim() ? `Phone: ${formData.phone.trim()}` : null,
    ]
      .filter(Boolean)
      .join('\n\n');

    try {
      await contactAPI.submit({
        firstName: formData.firstName.trim(),
        lastName: formData.lastName.trim(),
        email: formData.email.trim(),
        helpType: formData.helpType,
        message: enrichedMessage,
      });

      setSubmitStatus('success');
      setFormData({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        company: '',
        helpType: 'Product Information',
        message: '',
      });
    } catch (error) {
      console.error('Failed to submit contact form:', error);
      setSubmitStatus('error');
      setErrorMessage('We could not send your message. Please email business@ailyticslabs.com directly.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-950">
      <section className="bg-slate-950 pt-28 text-white">
        <div className="mx-auto grid max-w-7xl gap-12 px-6 pb-16 pt-10 lg:grid-cols-[1.05fr_0.95fr] lg:items-end">
          <div>
            <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-cyan-300/30 bg-cyan-300/10 px-4 py-2 text-sm font-semibold text-cyan-100">
              <ShieldCheck className="h-4 w-4" />
              Production support and commercial inquiries
            </div>
            <h1 className="max-w-4xl text-5xl font-bold leading-tight tracking-normal md:text-6xl">
              Contact Allytic Labs
            </h1>
            <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-300">
              Reach the team responsible for robotics, drone operations, and solar technology deployments. Send a focused inquiry and we will route it to the right desk.
            </p>
          </div>

          <div className="grid gap-4 sm:grid-cols-3 lg:grid-cols-1">
            {responseCommitments.map((item) => (
              <div key={item} className="flex items-start gap-3 border-l border-cyan-300/40 pl-4">
                <CheckCircle className="mt-0.5 h-5 w-5 shrink-0 text-cyan-300" />
                <p className="text-sm leading-6 text-slate-200">{item}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <main className="mx-auto grid max-w-7xl gap-8 px-6 py-12 lg:grid-cols-[minmax(0,1fr)_390px]">
        <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm md:p-8">
          <div className="mb-8 flex flex-col justify-between gap-4 border-b border-slate-200 pb-6 md:flex-row md:items-start">
            <div>
              <h2 className="text-2xl font-bold text-slate-950">Send a message</h2>
              <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600">
                Include the product area, location, timeline, and any technical requirements so we can respond with useful next steps.
              </p>
            </div>
            <div className="flex items-center gap-2 rounded-lg bg-slate-100 px-3 py-2 text-sm font-semibold text-slate-700">
              <Clock3 className="h-4 w-4 text-blue-600" />
              1 business day
            </div>
          </div>

          {submitStatus === 'success' && (
            <div className="mb-6 flex items-start gap-3 rounded-lg border border-emerald-200 bg-emerald-50 p-4">
              <CheckCircle className="mt-0.5 h-5 w-5 shrink-0 text-emerald-600" />
              <div>
                <p className="font-semibold text-emerald-950">Message received.</p>
                <p className="mt-1 text-sm text-emerald-800">Thank you. Our team will review your inquiry and respond through email.</p>
              </div>
            </div>
          )}

          {submitStatus === 'error' && (
            <div className="mb-6 flex items-start gap-3 rounded-lg border border-red-200 bg-red-50 p-4">
              <AlertCircle className="mt-0.5 h-5 w-5 shrink-0 text-red-600" />
              <div>
                <p className="font-semibold text-red-950">Message not sent.</p>
                <p className="mt-1 text-sm text-red-800">{errorMessage}</p>
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid gap-5 md:grid-cols-2">
              <label className="block">
                <span className="text-sm font-semibold text-slate-800">First name</span>
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleInputChange}
                  autoComplete="given-name"
                  required
                  className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-950 outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100"
                  placeholder="Victor"
                />
              </label>

              <label className="block">
                <span className="text-sm font-semibold text-slate-800">Last name</span>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleInputChange}
                  autoComplete="family-name"
                  className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-950 outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100"
                  placeholder="Odhiambo"
                />
              </label>
            </div>

            <div className="grid gap-5 md:grid-cols-2">
              <label className="block">
                <span className="text-sm font-semibold text-slate-800">Work email</span>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  autoComplete="email"
                  required
                  className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-950 outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100"
                  placeholder="name@company.com"
                />
              </label>

              <label className="block">
                <span className="text-sm font-semibold text-slate-800">Phone</span>
                <input
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleInputChange}
                  autoComplete="tel"
                  className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-950 outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100"
                  placeholder="+254 700 000 000"
                />
              </label>
            </div>

            <div className="grid gap-5 md:grid-cols-2">
              <label className="block">
                <span className="text-sm font-semibold text-slate-800">Company or organization</span>
                <input
                  type="text"
                  name="company"
                  value={formData.company}
                  onChange={handleInputChange}
                  autoComplete="organization"
                  className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-950 outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100"
                  placeholder="Company name"
                />
              </label>

              <label className="block">
                <span className="text-sm font-semibold text-slate-800">Inquiry type</span>
                <select
                  name="helpType"
                  value={formData.helpType}
                  onChange={handleInputChange}
                  className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-950 outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100"
                >
                  {inquiryTypes.map((type) => (
                    <option key={type} value={type}>{type}</option>
                  ))}
                </select>
              </label>
            </div>

            <label className="block">
              <span className="text-sm font-semibold text-slate-800">Project details</span>
              <textarea
                name="message"
                value={formData.message}
                onChange={handleInputChange}
                rows={7}
                required
                className="mt-2 w-full resize-y rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-950 outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100"
                placeholder="Tell us what you want to build, buy, deploy, repair, or discuss..."
              />
              <span className="mt-2 block text-right text-xs text-slate-500">{messageCharacters} characters</span>
            </label>

            <div className="flex flex-col gap-4 border-t border-slate-200 pt-6 sm:flex-row sm:items-center sm:justify-between">
              <p className="max-w-xl text-sm leading-6 text-slate-600">
                By submitting, you agree to be contacted about this inquiry. For careers, email recruitment directly.
              </p>
              <button
                type="submit"
                disabled={isSubmitting || !canSubmit}
                className="inline-flex min-h-12 items-center justify-center gap-2 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-slate-400"
              >
                {isSubmitting ? (
                  <>
                    <span className="h-5 w-5 animate-spin rounded-full border-2 border-white border-t-transparent" />
                    Sending
                  </>
                ) : (
                  <>
                    <Send className="h-5 w-5" />
                    Send Message
                  </>
                )}
              </button>
            </div>
          </form>
        </section>

        <aside className="space-y-6">
          <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-lg font-bold text-slate-950">Contact channels</h2>
            <div className="mt-5 space-y-5">
              {contactChannels.map(({ label, detail, value, href, icon: Icon }) => (
                <a key={label} href={href} className="group flex gap-4 rounded-lg border border-slate-200 p-4 transition hover:border-blue-300 hover:bg-blue-50">
                  <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg bg-slate-100 text-blue-700 group-hover:bg-white">
                    <Icon className="h-5 w-5" />
                  </span>
                  <span>
                    <span className="block font-semibold text-slate-950">{label}</span>
                    <span className="mt-1 block text-sm leading-5 text-slate-600">{detail}</span>
                    <span className="mt-2 block text-sm font-semibold text-blue-700">{value}</span>
                  </span>
                </a>
              ))}
            </div>
          </section>

          <section className="rounded-lg bg-slate-950 p-6 text-white shadow-sm">
            <div className="flex items-center gap-3">
              <MapPin className="h-5 w-5 text-cyan-300" />
              <h2 className="text-lg font-bold">Nairobi office</h2>
            </div>
            <p className="mt-4 text-sm leading-6 text-slate-300">
              P.O. Box 00100<br />
              Nairobi, Kenya
            </p>
            <div className="mt-6 border-t border-white/10 pt-6">
              <p className="text-sm font-semibold text-white">Business hours</p>
              <dl className="mt-3 space-y-2 text-sm text-slate-300">
                <div className="flex justify-between gap-6">
                  <dt>Monday - Friday</dt>
                  <dd className="font-semibold text-white">9:00 - 18:00</dd>
                </div>
                <div className="flex justify-between gap-6">
                  <dt>Saturday</dt>
                  <dd className="font-semibold text-white">10:00 - 16:00</dd>
                </div>
                <div className="flex justify-between gap-6">
                  <dt>Sunday</dt>
                  <dd className="font-semibold text-white">Closed</dd>
                </div>
              </dl>
            </div>
          </section>

          <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
            <div className="flex items-center gap-3">
              <MessageSquare className="h-5 w-5 text-blue-700" />
              <h2 className="text-lg font-bold text-slate-950">Route your inquiry</h2>
            </div>
            <div className="mt-5 grid gap-3">
              <button onClick={() => navigate('/robots')} className="flex items-center justify-between rounded-lg border border-slate-200 px-4 py-3 text-left text-sm font-semibold text-slate-800 transition hover:border-blue-300 hover:bg-blue-50">
                Robotics solutions <ArrowRight className="h-4 w-4" />
              </button>
              <button onClick={() => navigate('/drones')} className="flex items-center justify-between rounded-lg border border-slate-200 px-4 py-3 text-left text-sm font-semibold text-slate-800 transition hover:border-blue-300 hover:bg-blue-50">
                Drone systems <ArrowRight className="h-4 w-4" />
              </button>
              <button onClick={() => navigate('/solarpanels')} className="flex items-center justify-between rounded-lg border border-slate-200 px-4 py-3 text-left text-sm font-semibold text-slate-800 transition hover:border-blue-300 hover:bg-blue-50">
                Solar panels <ArrowRight className="h-4 w-4" />
              </button>
            </div>
          </section>
        </aside>
      </main>

      <footer className="border-t border-slate-800 bg-slate-950 py-12 text-white">
        <div className="mx-auto grid max-w-7xl gap-8 px-6 md:grid-cols-[1.2fr_0.8fr_0.8fr_0.8fr]">
          <div>
            <BrandLogo />
            <p className="mt-5 max-w-sm text-sm leading-6 text-slate-400">
              Building practical technology across robotics, autonomous systems, and renewable energy.
            </p>
          </div>

          <div>
            <h3 className="font-semibold">Solutions</h3>
            <ul className="mt-4 space-y-3 text-sm text-slate-400">
              <li><Link to="/robots" className="hover:text-white">Robotics</Link></li>
              <li><Link to="/drones" className="hover:text-white">Drones</Link></li>
              <li><Link to="/solarpanels" className="hover:text-white">Solar Panels</Link></li>
            </ul>
          </div>

          <div>
            <h3 className="font-semibold">Company</h3>
            <ul className="mt-4 space-y-3 text-sm text-slate-400">
              <li><Link to="/company" className="hover:text-white">Company</Link></li>
              <li><Link to="/contact" className="hover:text-white">Contact</Link></li>
              <li><a href="mailto:recruitment@ailyticslabs.com" className="hover:text-white">Careers</a></li>
            </ul>
          </div>

          <div>
            <h3 className="font-semibold">Social</h3>
            <div className="mt-4 flex gap-3">
              <a href="https://www.instagram.com/ailyticslabs" target="_blank" rel="noopener noreferrer" className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/10 text-white transition hover:bg-white/20" aria-label="Instagram">
                <Instagram className="h-5 w-5" />
              </a>
              <a href="https://github.com/ailyticslabs" target="_blank" rel="noopener noreferrer" className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/10 text-white transition hover:bg-white/20" aria-label="GitHub">
                <Github className="h-5 w-5" />
              </a>
              <a href="https://www.youtube.com/@ailyticslabs" target="_blank" rel="noopener noreferrer" className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/10 text-white transition hover:bg-white/20" aria-label="YouTube">
                <Youtube className="h-5 w-5" />
              </a>
              <a href="https://www.linkedin.com/company/ailyticslabs" target="_blank" rel="noopener noreferrer" className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/10 text-white transition hover:bg-white/20" aria-label="LinkedIn">
                <Linkedin className="h-5 w-5" />
              </a>
            </div>
          </div>
        </div>

        <div className="mx-auto mt-10 max-w-7xl border-t border-slate-800 px-6 pt-6 text-sm text-slate-500">
          <p>&copy; 2026 Allytic Labs. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default Contact;
