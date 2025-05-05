import React from 'react';
import Layout from '@theme/Layout';
import HeroSection from '../components/HeroSection/HeroSection';
import FeatureStrip from '../components/FeatureStrip/FeatureStrip';
import CodeShowcase from '../components/CodeShowcase/CodeShowcase';
import CTASection from '../components/CTASection/CTASection';

export default function Home(): JSX.Element {
    return (
        <Layout
            title="Collektive"
            description="A modern Kotlin framework for building aggregate applications">
            <HeroSection />
            <FeatureStrip />
            <CodeShowcase />
            <CTASection />
        </Layout>
    );
}