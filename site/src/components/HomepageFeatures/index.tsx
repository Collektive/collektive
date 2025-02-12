import clsx from 'clsx';
import Heading from '@theme/Heading';
import CodeBlock from '@theme/CodeBlock';
import styles from './styles.module.css';

const codeNeighbor = `fun Aggregate<Int>.gradient(distanceSensor: DistanceSensor, source: Boolean): Double =
    share(POSITIVE_INFINITY) {
        val dist = distances()
        when {
            source -> 0.0
            else -> (it + dist).min(POSITIVE_INFINITY)
        }
    }
`

const KotlinLogo = require('@site/static/img/KotlinLogo.svg').default

export default function HomepageFeatures(): JSX.Element {
    return (
        <section className={styles.features}>
            <div className="container">
                <div className="row">
                    <div className={clsx('col col--4')}>
                        <CodeBlock language="kotlin">{codeNeighbor}</CodeBlock>
                        <div className="text--center padding-horiz--md">
                            <Heading as="h3">Practical Aggregate Language</Heading>
                            <p>TODO</p>
                        </div>
                    </div>

                    <div className={clsx('col col--4')}>
                        <div className="text--center">
                            <img src={String(require('@site/static/img/graftWithMoreLeaders.gif').default)} alt=""
                                 className={styles.featureSvg}/>
                        </div>
                        <div className="text--center padding-horiz--md">
                            <Heading as="h3">Large-scale Network Simulation</Heading>
                            <p>TODO</p>
                        </div>
                    </div>

                    <div className={clsx('col col--4')}>
                        <div className="text--center">
                            <KotlinLogo className={styles.featureSvg}/>
                        </div>
                        <div className="text--center padding-horiz--md">
                            <Heading as="h3">Kotlin Multiplatform Enabled</Heading>
                            <p>TODO</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}
