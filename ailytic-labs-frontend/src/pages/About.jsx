import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, BriefcaseBusiness, ChevronDown, Cpu, Drone, History, Leaf, Menu, Newspaper, X, Zap } from 'lucide-react';
import SiteFooter from '../components/SiteFooter';
import roboticsVideo from '../assets/advanced robotics.mp4';
import robotOneVideo from '../assets/robot1.mp4';
import robotTwoVideo from '../assets/robot2.mp4';
import robotVideo from '../assets/robot.mp4';
import droneVideo from '../assets/drone.mp4';
import solarVideo from '../assets/solar.mp4';
import solarEnergyVideo from '../assets/solar energy.mp4';
import solarPanelsVideo from '../assets/solar panels.mp4';

const storySections = [
  {
    id: 'the-automation-moment',
    title: 'The Automation Moment',
    eyebrow: 'Practical intelligence',
    description:
      'Robotics, aerial systems, and renewable energy are moving from isolated experiments into everyday operations. Bella exists to help organizations make that transition with discipline.',
    media: robotOneVideo,
    icon: Cpu,
  },
  {
    id: 'robotics-for-the-field',
    title: 'Robotics for the Field',
    eyebrow: 'Machines that work',
    description:
      'We focus on robots that can inspect, move, support, test, clean, and assist in real environments where reliability matters more than a showroom demonstration.',
    media: robotTwoVideo,
    icon: Zap,
  },
  {
    id: 'connecting-physical-operations',
    title: 'Connecting Physical Operations',
    eyebrow: 'Aerial visibility',
    description:
      'Drones extend the reach of teams that manage farms, infrastructure, sites, and assets. Our work centers on useful payloads, route planning, monitoring, and safe operation.',
    media: droneVideo,
    icon: Drone,
  },
  {
    id: 'energy-resilience',
    title: 'Energy Resilience',
    eyebrow: 'Sustainable deployment',
    description:
      'Solar systems make technology more dependable by reducing operating risk and energy uncertainty. We connect product selection with site planning and long-term support.',
    media: solarVideo,
    icon: Leaf,
  },
  {
    id: 'industrial-automation',
    title: 'Industrial Automation',
    eyebrow: 'Resilient production',
    description:
      'Automation succeeds when machines, operators, and data work together. Bella designs systems that support production goals without adding unnecessary operational friction.',
    media: robotVideo,
    icon: Cpu,
  },
  {
    id: 'energy-systems',
    title: 'Energy Systems',
    eyebrow: 'Cleaner operations',
    description:
      'Energy infrastructure is part of the operating stack. We help teams plan solar capacity, storage readiness, and dependable deployment paths for real sites.',
    media: solarEnergyVideo,
    icon: Leaf,
  },
  {
    id: 'solar-infrastructure',
    title: 'Solar Infrastructure',
    eyebrow: 'Power at scale',
    description:
      'From panels to field installation, our work turns renewable energy into practical infrastructure that supports homes, facilities, and distributed teams.',
    media: solarPanelsVideo,
    icon: Leaf,
  },
];

const anchors = [
  ['#body-of-work', 'Our Body of Work'],
  ...storySections.map(({ id, title }) => [`#${id}`, title]),
];

const resourceLinks = [
  ['History', 'How Bella is building its technology platform', '/about', History],
  ['Newsroom', 'Company updates and deployment notes', '/news', Newspaper],
  ['Careers', 'Join the team building practical technology', '/careers', BriefcaseBusiness],
  ['Partners', 'Work with us across supply, deployment, and channels', '/partners', ArrowRight],
];

const subnavMenus = [
  {
    label: 'Company',
    links: [
      ['History', '/about#body-of-work'],
      ['Executive Bios', '/about#like-no-place-you-have-worked'],
      ['Investors', '/partners'],
      ['Venture Capital', '/partners'],
      ['Brand Guidelines', '/about#body-of-work'],
      ['Inclusion', '/careers'],
    ],
  },
  {
    label: 'Our Work',
    links: [
      ['Robotics', '/robots'],
      ['Drones', '/drones'],
      ['Solar Panels', '/solarpanels'],
    ],
  },
  {
    label: 'Careers',
    links: [
      ['Open Roles', '/careers'],
      ['Culture', '/about#like-no-place-you-have-worked'],
      ['Contact Recruiting', 'mailto:recruitment@ailyticslabs.com'],
    ],
  },
  {
    label: 'News',
    links: [
      ['Company Updates', '/news'],
      ['Contact Media', 'mailto:business@ailyticslabs.com'],
    ],
  },
  {
    label: 'Events',
    links: [
      ['Request Demo', '/demo'],
      ['Partnership Events', '/partners'],
      ['Contact Team', '/contact'],
    ],
  },
];

function About() {
  const [openMenu, setOpenMenu] = useState(null);
  const [isDrawerOpen, setIsDrawerOpen] = useState(true);
  const [activeSectionId, setActiveSectionId] = useState('body-of-work');

  useEffect(() => {
    const sectionIds = anchors.map(([href]) => href.slice(1));
    const sections = sectionIds
      .map((id) => document.getElementById(id))
      .filter(Boolean);
    let ticking = false;

    const updateHash = (id) => {
      const nextUrl = `${window.location.pathname}#${id}`;
      if (window.location.hash !== `#${id}`) {
        window.history.replaceState(null, '', nextUrl);
      }
    };

    const setHashFromScrollPosition = () => {
      const viewportAnchor = window.scrollY + window.innerHeight * 0.42;
      const activeSection = sections.reduce((current, section) => {
        const sectionTop = section.offsetTop;
        return sectionTop <= viewportAnchor ? section : current;
      }, sections[0]);

      if (activeSection?.id) {
        updateHash(activeSection.id);
        setActiveSectionId(activeSection.id);
      }

      ticking = false;
    };

    const handleScroll = () => {
      if (!ticking) {
        window.requestAnimationFrame(setHashFromScrollPosition);
        ticking = true;
      }
    };

    setHashFromScrollPosition();
    window.addEventListener('scroll', handleScroll, { passive: true });
    window.addEventListener('resize', handleScroll);

    return () => {
      window.removeEventListener('scroll', handleScroll);
      window.removeEventListener('resize', handleScroll);
    };
  }, []);

  return (
    <div className="min-h-screen bg-white text-slate-950">
      <section id="body-of-work" className="about-video-section relative min-h-[calc(100vh-4rem)] scroll-mt-24 overflow-hidden bg-black pt-16 text-white">
        <div className="absolute inset-0">
          <video
            className="h-full w-full object-cover opacity-45 grayscale"
            autoPlay
            muted
            loop
            playsInline
          >
            <source src={roboticsVideo} type="video/mp4" />
          </video>
          <div className="absolute inset-0 bg-gradient-to-r from-black via-black/70 to-black/35" />
        </div>

        <div className="relative z-20 border-b border-white/10 bg-black">
          <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-6">
            <div className="flex items-center gap-8">
              <a href="#body-of-work" className="text-2xl font-bold text-white">About Us</a>
              <nav className="hidden items-center gap-7 md:flex">
                {subnavMenus.map((menu) => (
                  <div
                    key={menu.label}
                    className="relative"
                    onMouseEnter={() => setOpenMenu(menu.label)}
                    onMouseLeave={() => setOpenMenu(null)}
                  >
                    <button
                      type="button"
                      onClick={() => setOpenMenu(openMenu === menu.label ? null : menu.label)}
                      className={`flex items-center gap-1 text-lg transition ${
                        openMenu === menu.label ? 'text-cyan-300' : 'text-slate-300 hover:text-white'
                      }`}
                      aria-expanded={openMenu === menu.label}
                    >
                      {menu.label}
                      <ChevronDown className="h-4 w-4" />
                    </button>
                    {openMenu === menu.label && (
                      <div className="absolute left-0 top-8 z-30 min-w-72 bg-white px-5 py-6 shadow-2xl">
                        <span className="absolute -top-3 left-6 h-6 w-6 rotate-45 bg-white" />
                        {menu.links.map(([label, href]) => (
                          <a
                            key={label}
                            href={href}
                            className="relative z-10 block py-3 text-base text-zinc-500 transition hover:text-zinc-950"
                          >
                            {label}
                          </a>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </nav>
            </div>
            <button
              type="button"
              onClick={() => setIsDrawerOpen(true)}
              className="inline-flex h-10 w-10 items-center justify-center rounded-full border border-white/15 text-white transition hover:bg-white/10"
              aria-label="Open about menu"
            >
              <Menu className="h-5 w-5" />
            </button>
          </div>
        </div>

        <div className="relative z-10 mx-auto flex min-h-[calc(100vh-8rem)] max-w-7xl items-center px-6 py-16">
          <div className="about-section-copy max-w-2xl">
            <h1 className="text-5xl font-bold leading-tight tracking-normal text-white/70 md:text-7xl">
              Our Body of Work
            </h1>
            <p className="mt-8 max-w-xl text-xl leading-9 text-white/60">
              Bella builds practical technology across robotics, drones, and solar energy for organizations that need smarter operations, cleaner power, and dependable automation.
            </p>
          </div>
          <a href="#the-automation-moment" className="absolute bottom-8 left-1/2 hidden -translate-x-1/2 text-center text-white/50 md:block">
            <span className="block text-sm">Explore</span>
            <span className="mt-1 block text-2xl leading-none">↓</span>
          </a>
        </div>

        {isDrawerOpen && (
          <div className="fixed right-0 top-16 z-30 h-[calc(100vh-4rem)] w-80 max-w-[86vw] bg-zinc-950/95 px-8 py-8 text-white shadow-2xl lg:absolute lg:h-[calc(100%-4rem)]">
            <button
              type="button"
              onClick={() => setIsDrawerOpen(false)}
              className="absolute right-6 top-6 inline-flex h-9 w-9 items-center justify-center rounded-full bg-white/15 text-white transition hover:bg-white/25"
              aria-label="Close about menu"
            >
              <X className="h-5 w-5" />
            </button>
            <p className="mb-8 text-2xl font-bold tracking-normal">BELLA</p>
            <nav className="space-y-4 text-slate-300">
              {anchors.map(([href, label]) => (
                <a key={href} href={href} className="block text-base leading-7 transition hover:text-white">
                  {label}
                </a>
              ))}
            </nav>
            <div className="mt-10 border-t border-white/30 pt-8">
              <h2 className="text-2xl font-bold">About Us</h2>
              <div className="mt-4 space-y-3 text-slate-300">
                <Link to="/about" className="block hover:text-white">Company</Link>
                <Link to="/careers" className="block hover:text-white">Careers</Link>
                <Link to="/news" className="block hover:text-white">News</Link>
                <Link to="/partners" className="block hover:text-white">Partners</Link>
              </div>
            </div>
          </div>
        )}

        <a href="/contact" className="fixed right-0 top-1/2 z-40 hidden -translate-y-1/2 rotate-180 bg-white px-3 py-4 text-sm font-semibold text-black shadow-lg [writing-mode:vertical-rl] lg:block">
          Feedback
        </a>

        <div className="fixed right-14 top-1/2 z-40 hidden -translate-y-1/2 flex-col items-center gap-3 lg:flex" aria-label="About page sections">
          {anchors.map(([href, label]) => (
            <a
              key={href}
              href={href}
              className={`h-3 w-3 rounded-full border border-white/80 shadow transition ${
                activeSectionId === href.slice(1) ? 'bg-white' : 'bg-white/35 hover:bg-white/70'
              }`}
              aria-label={`Go to ${label}`}
              aria-current={activeSectionId === href.slice(1) ? 'true' : undefined}
            />
          ))}
        </div>
      </section>

      <main>
        {storySections.map(({ id, title, eyebrow, description, media, icon: Icon }, index) => (
          <section key={id} id={id} className="about-video-section relative min-h-screen scroll-mt-24 overflow-hidden bg-slate-950 text-white">
            <video
              className="absolute inset-0 h-full w-full object-cover opacity-65"
              autoPlay
              muted
              loop
              playsInline
            >
              <source src={media} type="video/mp4" />
            </video>
            <div className={`absolute inset-0 ${
              index % 2 === 0
                ? 'bg-gradient-to-r from-black via-black/70 to-black/20'
                : 'bg-gradient-to-l from-black via-black/70 to-black/20'
            }`} />

            <div className={`relative z-10 mx-auto flex min-h-screen max-w-7xl items-center px-6 py-20 ${
              index % 2 === 1 ? 'justify-end' : 'justify-start'
            }`}>
              <div className="about-section-copy max-w-2xl">
                <div className="mb-5 flex h-12 w-12 items-center justify-center rounded-lg bg-white/10 text-cyan-300 backdrop-blur">
                  <Icon className="h-6 w-6" />
                </div>
                <p className="text-sm font-bold uppercase text-cyan-300">{eyebrow}</p>
                <h2 className="mt-3 text-4xl font-bold tracking-normal text-white md:text-6xl">{title}</h2>
                <p className="mt-6 text-xl leading-9 text-white/75">{description}</p>
              </div>
            </div>
          </section>
        ))}

        <section id="like-no-place-you-have-worked" className="scroll-mt-24 bg-slate-950 text-white">
          <div className="mx-auto grid max-w-7xl gap-10 px-6 py-20 lg:grid-cols-[0.9fr_1.1fr] lg:items-center">
            <div>
              <p className="text-sm font-bold uppercase text-cyan-300">Culture</p>
              <h2 className="mt-4 text-4xl font-bold tracking-normal md:text-5xl">Like no place you have worked.</h2>
            </div>
            <div>
              <p className="text-2xl font-semibold leading-10 text-slate-100">
                “The mission is to make advanced technology useful outside perfect conditions. Everyone here is expected to think clearly, build carefully, and stay close to the customer.”
              </p>
              <Link to="/careers" className="mt-8 inline-flex items-center gap-2 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition hover:bg-blue-700">
                See careers
                <ArrowRight className="h-5 w-5" />
              </Link>
            </div>
          </div>
        </section>

        <section className="bg-white">
          <div className="mx-auto grid max-w-7xl gap-5 px-6 py-16 md:grid-cols-4">
            {resourceLinks.map(([title, description, href, Icon]) => (
              <Link key={title} to={href} className="group rounded-lg border border-slate-200 bg-white p-6 shadow-sm transition hover:border-blue-300 hover:bg-blue-50">
                <Icon className="h-6 w-6 text-blue-700" />
                <h3 className="mt-5 text-xl font-bold text-slate-950">{title}</h3>
                <p className="mt-3 text-sm leading-6 text-slate-600">{description}</p>
                <span className="mt-5 inline-flex items-center gap-2 text-sm font-semibold text-blue-700 group-hover:text-blue-800">
                  Explore
                  <ArrowRight className="h-4 w-4" />
                </span>
              </Link>
            ))}
          </div>
        </section>
      </main>

      <SiteFooter />
    </div>
  );
}

export default About;
